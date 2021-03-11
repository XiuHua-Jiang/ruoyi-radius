package com.ruoyi.radius.toughradius.service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import com.ruoyi.radius.tinyradius.packet.AccessRequest;
import com.ruoyi.radius.tinyradius.packet.AccountingRequest;
import com.ruoyi.radius.tinyradius.packet.CoaRequest;
import com.ruoyi.radius.tinyradius.packet.RadiusPacket;
import com.ruoyi.radius.tinyradius.util.RadiusClient;
import com.ruoyi.radius.tinyradius.util.RadiusException;
import com.ruoyi.radius.toughradius.common.DateTimeUtil;
import com.ruoyi.radius.toughradius.common.PageResult;
import com.ruoyi.radius.toughradius.common.SpinLock;
import com.ruoyi.radius.toughradius.common.ValidateUtil;
import com.ruoyi.radius.toughradius.config.RadiusConfig;
import com.ruoyi.radius.toughradius.domain.Bras;
import com.ruoyi.radius.toughradius.domain.RadiusOnline;
import com.ruoyi.radius.toughradius.domain.Subscribe;
import com.ruoyi.radius.toughradius.form.FreeradiusAcctRequest;

import com.alibaba.fastjson.JSONObject;

/**
 * 在线用户缓存业务类
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Service
public class OnlineCache {

    private final static ConcurrentLinkedDeque<CoaRequest> coaque = new  ConcurrentLinkedDeque<>();

    private final static  HashMap<String, RadiusOnline> cacheData = new HashMap<String,RadiusOnline>();
    @Autowired
    private Memarylogger logger;

    @Autowired
    private IBrasService brasService;
    
    @Autowired
    private ISubscribeService subscribeService;

    @Autowired
    private SubscribeCache subscribeCache;

    @Autowired
    private RadiusConfig radiusConfig;

    @Autowired
    private ThreadPoolTaskExecutor systaskExecutor;

    private final static SpinLock lock = new SpinLock();


    public HashMap<String,RadiusOnline> getCacheData(){
        return cacheData;
    }

    public CoaRequest peekCoaRequest(){
        return coaque.removeFirst();
    }

    public long[] getOnlineStat(){
        ArrayList<RadiusOnline> copy = new ArrayList<>(cacheData.values());
        long v1 = copy.stream().filter(x->!isExpire(x,radiusConfig.getInterimUpdate())).count();
        long v2 = copy.size() - v1;
        return new long[]{v1, v2};
    }


    public int size()
    {
        return cacheData.size();
    }

    public List<RadiusOnline> getReadonlyOnlineList()
    {
        try {
            lock.lock();
            return new ArrayList<>(Collections.unmodifiableCollection(cacheData.values()));
        }
        finally {
            lock.unLock();
        }
    }

    public List<RadiusOnline> list(String nodeId)
    {
        List<RadiusOnline> onlineList = new ArrayList<RadiusOnline>();
        try{
            lock.lock();
            for (RadiusOnline online : cacheData.values()) {
                if (online != null && online.getNodeId().equals(nodeId))
                    onlineList.add(online);
            }
        }finally {
            lock.unLock();
        }
        return onlineList;
    }

    public List<RadiusOnline> queryNoAmountOnline( )
    {
        List<RadiusOnline> onlineList = new ArrayList<RadiusOnline>();
        try{
            lock.lock();
            for (RadiusOnline online : cacheData.values()) {
                if (online.getUnLockFlag() == RadiusOnline.AMOUNT_NOT_ENOUGH)
                    onlineList.add(online);
            }
        } finally {
            lock.unLock();
        }
        return onlineList;
    }

    public boolean isExist(String sessionId)
    {
        try{
            lock.lock();
            return cacheData.containsKey(sessionId);
        } finally {
            lock.unLock();
        }
    }

    public RadiusOnline getOnline(String sessionId)
    {
        try{
            lock.lock();
            return cacheData.get(sessionId);
        } finally {
            lock.unLock();
        }
    }

    public boolean isOnline(String userName)
    {
        try{
            lock.lock();
            boolean isTcRadiusOnline = false;
            for (RadiusOnline online : cacheData.values()) {
                if (userName.equals(online.getUsername())) {
                    isTcRadiusOnline = true;
                    break;
                }
            }
            return isTcRadiusOnline;
        } finally {
            lock.unLock();
        }
    }

    /**
     * 异步批量下线
     * @param ids
     */
    public void unlockOnlines(List<String> ids)
    {
        for(String sessionid : ids){
            asyncUnlockOnline(sessionid);
        }
    }

    /**
     * 异步批量下线
     * @param ids
     */
    public void unlockOnlines(String ids)
    {
        for(String sessionid : ids.split(",")){
            asyncUnlockOnline(sessionid);
        }
    }

    /**
     * 单个下线
     * @param sessionId
     */
    public boolean unlockOnline(String sessionId)
    {
        RadiusOnline online = getOnline(sessionId);
        try {
            if(online==null){
                logger.error("发送下线失败,无在线信息", Memarylogger.RADIUSD_COA);
                return false;
            }


            Bras bras = brasService.findBras(online.getNasAddr(),null, online.getNasId());
            RadiusClient cli = new RadiusClient(online.getNasPaddr(),bras.getSecret());
            
            // update by pwl 改用chap方式  默认的是pap  routeos的不是pap方式  后面再视情况修改
            cli.setAuthProtocol(AccessRequest.AUTH_CHAP);
            
            CoaRequest dmreq = new CoaRequest(RadiusPacket.DISCONNECT_REQUEST);
            dmreq.addAttribute("User-Name",online.getUsername());
            dmreq.addAttribute("Acct-Session-Id",online.getAcctSessionId());
            // update by pwl MikroTik路由    传客户机IP而非路由IP 
            //dmreq.addAttribute("NAS-IP-Address",online.getNasAddr());
            dmreq.addAttribute("Framed-IP-Address",online.getFramedIpaddr());
            
            logger.info(online.getUsername(), "发送下线请求 " + dmreq.toLineString(), Memarylogger.RADIUSD_COA);
            if(online.isRadsec()){
                coaque.addLast(dmreq);
                return true;
            }
            RadiusPacket dmrep = cli.communicate(dmreq,bras.getCoaPort());
            logger.info(online.getUsername(), "接收到下线响应 " + dmrep.toLineString(), Memarylogger.RADIUSD_COA);
            return dmrep.getPacketType() == RadiusPacket.DISCONNECT_ACK;
        } catch (ServiceException | IOException | RadiusException e) {
            logger.error(online.getUsername(),"发送下线失败",e, Memarylogger.RADIUSD_COA);
            //removeOnline(sessionId);
            return false;
        }
    }

    public void asyncUnlockOnline(String sessionid){
        RadiusOnline online = getOnline(sessionid);
        if(online==null){
            logger.error("发送下线失败,无在线信息", Memarylogger.RADIUSD_COA);
            return;
        }
        systaskExecutor.execute(()->{
            try {
                Bras bras = brasService.findBras(online.getNasAddr(),null,online.getNasId());
                if(bras==null){
                    logger.error(online.getUsername(),
                            "发送下线失败,查找BRAS失败（nasid=" + online.getNasId() + ",nasip=" + online.getNasAddr() + "）", Memarylogger.RADIUSD_COA);
                    return;
                }
                logger.info("-------->online = " + JSONObject.toJSONString(online), Memarylogger.RADIUSD_COA);
                logger.info("-------->bras = " + JSONObject.toJSONString(bras), Memarylogger.RADIUSD_COA);
                
                RadiusClient cli = new RadiusClient(online.getNasPaddr(),bras.getSecret());
                
                // update by pwl 改用chap方式  默认的是pap  routeos的不是pap方式  后面再视情况修改
                cli.setAuthProtocol(AccessRequest.AUTH_CHAP);
                
                CoaRequest dmreq = new CoaRequest(RadiusPacket.DISCONNECT_REQUEST);
                dmreq.addAttribute("User-Name",online.getUsername());
                dmreq.addAttribute("Acct-Session-Id",online.getAcctSessionId());
                // update by pwl MikroTik路由    传客户机IP而非路由IP 
                //dmreq.addAttribute("NAS-IP-Address",online.getNasAddr());
                dmreq.addAttribute("Framed-IP-Address",online.getFramedIpaddr());
                logger.info(online.getUsername(), "发送下线请求 " + dmreq.toLineString(), Memarylogger.RADIUSD_COA);
                if(online.isRadsec()){
                    coaque.addLast(dmreq);
                    return;
                }
                RadiusPacket dmrep = cli.communicate(dmreq,bras.getCoaPort());
                logger.info(online.getUsername(), String.format("接收到下线响应 %s", dmrep.toLineString()), Memarylogger.RADIUSD_COA);
            } catch (ServiceException | IOException | RadiusException e) {
                logger.error(online.getUsername(),"发送下线失败",e, Memarylogger.RADIUSD_COA);
                //removeOnline(sessionid);
            }
        });
    }


    public List<RadiusOnline> getOnlineByUserName(String userName)
    {
        try{
            lock.lock();
            List<RadiusOnline> onlineList = new ArrayList<>();
            for (RadiusOnline online : cacheData.values()) {
                if (userName.equalsIgnoreCase(online.getUsername())) {
                    onlineList.add(online);
                }
            }
            return onlineList;
        } finally {
            lock.unLock();
        }
    }

    /** 用户上线 */
    public void putOnline(RadiusOnline online)
    {
        try{
            lock.lock();
            String key = online.getAcctSessionId();
            cacheData.put(key, online);
        } finally {
            lock.unLock();
        }
    }

    /** 一个用户下线 */
    public RadiusOnline removeOnline(String sessionId)
    {
        try{
            lock.lock();
            return cacheData.remove(sessionId);
        } finally {
            lock.unLock();
        }
    }


    /** 查询上网帐号并发数 */
    public int getUserOnlineNum(String userName)
    {

        try{
            lock.lock();
            int onlineNum = 0;
            for (RadiusOnline online : cacheData.values()) {
                if (userName.equalsIgnoreCase(online.getUsername()))
                    onlineNum++;
            }
            return onlineNum;
        } finally {
            lock.unLock();
        }
    }

    /** 查询上网帐号并发数 是否超过限制， 超过预设即返回，要求MAC地址不相等 避免多次循环*/
    public boolean isLimitOver(String userName, int size, String macAddr)
    {
        try{
            lock.lock();
            int onlineNum = 0;
            for (RadiusOnline online : cacheData.values()) {
                if (userName.equalsIgnoreCase(online.getUsername()) && !macAddr.equals(online.getMacAddr()))
                {
                    onlineNum ++;
                }
                if (onlineNum >= size){
                    return true;
                }
            }
            return onlineNum >= size;
        } finally {
            lock.unLock();
        }
    }

    /** 查询上网帐号并发数,要求MAC地址不相等 */
    public int getUserOnlineNum(String userName, String macAddr)
    {
        try{
            lock.lock();
            int onlineNum = 0;
            for (RadiusOnline online : cacheData.values()) {
                if (userName.equals(online.getUsername()) && !macAddr.equals(online.getMacAddr()))
                    onlineNum++;
            }
            return onlineNum;
        } finally {
            lock.unLock();
        }
    }

    /**
     * 间隔 interim_times 不更新的为过期
     * @param online
     * @param interim_times
     * @return
     */
    private  boolean isExpire(RadiusOnline online, int interim_times)
    {
        String curTime = DateTimeUtil.getDateTimeString();
        String acctStart =  online.getAcctStartTime();
        int second = DateTimeUtil.compareSecond(curTime,acctStart);
        if (second > (online.getAcctSessionTime()  + interim_times + 30))
            return true;
        else
            return false;
    }


    public void clearOvertimeTcRadiusOnline( int interim_times)
    {
        try{
            lock.lock();
            for (Iterator<RadiusOnline> it = cacheData.values().iterator(); it.hasNext();)
            {
                RadiusOnline online = it.next();
                if (!this.isExpire(online,interim_times))
                    continue;//直到没有超时的用户

                it.remove();
                System.out.println("BRAS[nasip="+online.getNasAddr()+",nasid="+online.getNasId()+"]:用户[user="+online.getUsername()+",session="+online.getAcctSessionId()+"]上线时间["+online.getAcctStartTime()+"]超时未收到更新消息，被自动清理。");
                //超时下线消息跟踪
                logger.info(online.getUsername(),"BRAS[nasip="+online.getNasAddr()+",nasid="+online.getNasId()+"]:用户[user="+online.getUsername()+",session="+online.getAcctSessionId()+"]上线时间["+online.getAcctStartTime()+"]超时未收到更新消息，被自动清理。");

            }
        } finally {
            lock.unLock();
        }
    }

    public void unlockExpireTcRadiusOnline()
    {

        List<String> ids = new ArrayList<>();
        try{
            lock.lock();
            for (Iterator<RadiusOnline> it = cacheData.values().iterator(); it.hasNext();)
            {
                RadiusOnline online = it.next();
                Subscribe user = subscribeCache.findSubscribe(online.getUsername());
                if(user == null)
                    continue;

                if(DateTimeUtil.compareSecond(new Date(),user.getExpireTime())>0){
                    ids.add(online.getAcctSessionId());
                }

                if(ids.size()>=32){
                    List<String> idscopy = new ArrayList<>(ids);
                    this.unlockOnlines(idscopy);
                    ids.clear();
                }
                System.out.println("BRAS[nasip="+online.getNasAddr()+",nasid="+online.getNasId()+"]:用户[user="+online.getUsername()+",session="+online.getAcctSessionId()+"]已经过期/或流量不足，即将发送下线指令。");
                //超时下线消息跟踪
                logger.info(online.getUsername(),"BRAS[nasip="+online.getNasAddr()+",nasid="+online.getNasId()+"]:用户[user="+online.getUsername()+",session="+online.getAcctSessionId()+"]已经过期/或流量不足，即将发送下线指令。");
            }
        } finally {
            lock.unLock();
        }
        this.unlockOnlines(ids);
    }

    public void updateOnline(AccountingRequest request) {
        try{
            lock.lock();
            RadiusOnline online = cacheData.get(request.getAcctSessionId());
            if(online!=null){
                online.setUsername(request.getUserName());
                online.setAcctSessionId(request.getAcctSessionId());
                online.setAcctSessionTime(request.getAcctSessionTime());
                online.setAcctInputTotal(request.getAcctInputTotal());
                online.setAcctOutputTotal(request.getAcctOutputTotal());
                online.setAcctInputPackets(request.getAcctInputPackets());
                online.setAcctOutputPackets(request.getAcctOutputPackets());
            }
        } finally {
            lock.unLock();
        }
    }

    public void updateOnline(FreeradiusAcctRequest request) {
        try{
            lock.lock();
            RadiusOnline online = cacheData.get(request.getAcctSessionId());
            if(online!=null){
                online.setUsername(request.getUsername());
                online.setAcctSessionId(request.getAcctSessionId());
                online.setAcctSessionTime(request.getAcctSessionTime());
                online.setAcctInputTotal(request.getAcctInputTotal());
                online.setAcctOutputTotal(request.getAcctOutputTotal());
                online.setAcctInputPackets(request.getAcctInputPackets());
                online.setAcctOutputPackets(request.getAcctOutputPackets());
            }
        } finally {
            lock.unLock();
        }
    }


    private boolean filterOnline(RadiusOnline online, String nodeId, Integer invlan, Integer outVlan, String nasAddr,
                                 String nasId, String beginTime, String endTime, String keyword) {
        if(ValidateUtil.isNotEmpty(nodeId)&&!nodeId.equalsIgnoreCase(online.getNodeId().toString())) {
            return false;
        }

        if(ValidateUtil.isNotEmpty(nasAddr)&&(!nodeId.equalsIgnoreCase(online.getNasAddr())&&!nodeId.equals(online.getNasPaddr()))) {
            return  false;
        }
        if(ValidateUtil.isNotEmpty(nasId)&&!nasId.equalsIgnoreCase(online.getNasId())) {
            return  false;
        }

        if(invlan!=null&&!invlan.equals(online.getInVlan())) {
            return  false;
        }
        if(outVlan!=null&&!outVlan.equals(online.getOutVlan())) {
            return  false;
        }

        if (ValidateUtil.isNotEmpty(beginTime)) {
            if(beginTime.length() == 16){
                beginTime = beginTime+ ":00";
            }
            if( DateTimeUtil.compareSecond(online.getAcctStartTime(), beginTime)<0){
                return  false;
            }
        }

        if (ValidateUtil.isNotEmpty(endTime)) {
            if(endTime.length()==16){
                endTime = endTime + ":59";
            }
            if( DateTimeUtil.compareSecond(online.getAcctStartTime(), endTime)>0){
                return  false;
            }
        }

        if(ValidateUtil.isNotEmpty(keyword)){
            if( (ValidateUtil.isNotEmpty(online.getUsername()) && online.getUsername().toLowerCase().contains(keyword.toLowerCase())) ||
                (ValidateUtil.isNotEmpty(online.getNasAddr()) && online.getNasAddr().contains(keyword)) ||
                (ValidateUtil.isNotEmpty(online.getNasPaddr()) && online.getNasPaddr().contains(keyword)) ||
                (ValidateUtil.isNotEmpty(online.getNasId()) && online.getNasId().contains(keyword)) ||
                (ValidateUtil.isNotEmpty(online.getNasPaddr()) && online.getNasPaddr().contains(keyword)) ||
                (ValidateUtil.isNotEmpty(online.getNasPortId()) && online.getNasPortId().contains(keyword)) ||
                (ValidateUtil.isNotEmpty(online.getFramedIpaddr())&& online.getFramedIpaddr().contains(keyword))||
                (ValidateUtil.isNotEmpty(online.getRealname())&& online.getRealname().contains(keyword))||
                (ValidateUtil.isNotEmpty(online.getMacAddr())&& online.getMacAddr().contains(keyword)) ){
                return  true;
            }else{
                return false;
            }
        }
        return true;
    }

    public PageResult<RadiusOnline> queryOnlinePage(int pos, int count, String nodeId,
                                                    Integer invlan, Integer outVlan, String nasAddr, String nasId,
                                                    String beginTime, String endTime, String keyword, String sort){
        int total = 0;
        int start = pos+1;
        int end = pos +  count ;
        List<RadiusOnline> copyList = getReadonlyOnlineList();;
        List<RadiusOnline> onlineList = new ArrayList<>();
        for (RadiusOnline online : copyList) {
            if (!this.filterOnline(online, nodeId, invlan,outVlan, nasAddr, nasId, beginTime, endTime, keyword)) {
                continue;
            }
            total++;
            if (total >= start && total <= end) {
                try {
                    onlineList.add(online.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
        return new PageResult<>(pos, total, onlineList);
    }

    /**
     * 查询在线用户
     * @param realName
     * @param userName
     * @return
     */
    public List<RadiusOnline> queryOnlinePage(String realName,String userName){
        List<RadiusOnline> copyList = getReadonlyOnlineList();;
        List<RadiusOnline> onlineList = new ArrayList<>();
        // 没有查询参数 直接返回总记录
        if(ValidateUtil.isEmpty(realName) && ValidateUtil.isEmpty(userName)){
            return copyList;
        }
        for (RadiusOnline online : copyList) {
            // 匹配查询条件
            if (!(online.getRealname().contains(realName) || online.getRealname().contains(userName))) {
                continue;
            }
            try {
                onlineList.add(online.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return onlineList;
    }

    public  List<RadiusOnline> queryOnlineByIds(String ids){
        try{
            lock.lock();
            String[] idarray = ids.split(",");
            List<RadiusOnline> onlineList = new ArrayList<>();
            for (String sid : idarray) {
                RadiusOnline online = cacheData.get(sid);
                if(online!=null){
                    onlineList.add(online);
                }
            }
            return onlineList;
        } finally {
            lock.unLock();
        }

    }


    /**
     * 根据用户名强制下线
     */
    public void unlockOnlineByUser(String username)
    {
        try{
            lock.unLock();
            for (RadiusOnline _online : cacheData.values()) {
                if (username.equals(_online.getUsername())) {
                    asyncUnlockOnline(_online.getAcctSessionId());
                }
            }
        }finally {
            lock.unLock();
        }
    }

    public int clearOnlineByFilter(String nodeId, Integer invlan,Integer outVlan,String nasAddr, String nasId, String beginTime, String endTime,  String keyword){
        try{
            lock.lock();
            int total = 0;
            for (Iterator<RadiusOnline> it = cacheData.values().iterator(); it.hasNext();)
            {
                RadiusOnline online = it.next();
                if(this.filterOnline(online, nodeId,   invlan, outVlan, nasAddr, nasId, beginTime, endTime,  keyword)) {
                    total++;
                    it.remove();
                }
            }
            return total;
        } finally {
            lock.unLock();
        }

    }

    public int clearOnlineByFilter(String nasAddr, String nasId){

        try{
            lock.lock();
            int total = 0;
            for (Iterator<RadiusOnline> it = cacheData.values().iterator(); it.hasNext();)
            {
                RadiusOnline online = it.next();
                if(this.filterOnline(online, null,  null,null, nasAddr, nasId, null, null,  null)) {
                    total++;
                    it.remove();
                }
            }
            return total;
        } finally {
            lock.unLock();
        }
    }
    
    /**
     * 
     * 根据用户名修改权限
     * 
     * 到期时间、上下行速率
     */
    public void DynamicAuthorizationByUserName(String username)
    {
        try{
            lock.unLock();
            for (RadiusOnline _online : cacheData.values()) {
                if (username.equals(_online.getUsername())) {
                	DynamicAuthorization(_online.getAcctSessionId());
                }
            }
        }finally {
            lock.unLock();
        }
    }
    /**
     * 
     * 动态授权
     * 
     * PS:不能动态修改用户名
     * 可以修改用户过期时间，即Session-Timeout 单位为秒
     * 可以修改上网速度（上行/下行） 单位MB
     * 
     * ？？？？？？？？ 具体可改参数和单位还需要根据实际情况进行修改
     * 
     * @param sessionId
     */
    public void DynamicAuthorization(String sessionId){
    	
        RadiusOnline online = getOnline(sessionId);
        
        if(online==null){
        	logger.error("发送动态授权失败,无在线信息", Memarylogger.RADIUSD_COA);
        }
        systaskExecutor.execute(()->{
	        try {
	            Bras bras = brasService.findBras(online.getNasAddr(),null, online.getNasId());
	            RadiusClient cli = new RadiusClient(online.getNasPaddr(),bras.getSecret());
	            
	            // update by pwl 改用chap方式  默认的是pap  routeos的不是pap方式  后面再视情况修改
	            cli.setAuthProtocol(AccessRequest.AUTH_CHAP);
	            
	            CoaRequest dmreq = new CoaRequest(RadiusPacket.COA_REQUEST);
	            
	            Subscribe user = subscribeService.findSubscribe(online.getUsername());
	            
	            // User-Name、Acct-Session-Id、Framed-IP-Address 三个属性为必传固定属性
	            dmreq.addAttribute("User-Name",online.getUsername());
	            dmreq.addAttribute("Acct-Session-Id",online.getAcctSessionId());
	            // update by pwl MikroTik路由    传客户机IP而非路由IP 
	            //dmreq.addAttribute("NAS-IP-Address",online.getNasAddr());
	            dmreq.addAttribute("Framed-IP-Address",online.getFramedIpaddr());
	            
	            /***** 以下为动态添加的属性，可选     ************/
	            
	            // 到期时间   单位秒
	            if(user.getExpireTime().getTime() > new Date().getTime()){
	            	dmreq.addAttribute("Session-Timeout",String.valueOf(DateTimeUtil.compareDay(new Date(user.getExpireTime().getTime()), new Date())));
	            }else{
	            	dmreq.addAttribute("Session-Timeout",String.valueOf(0));
	            }
	            /**
	             * 客户的速率限制。字符串表示，格式为 rx-rate[/tx-rate] [rx-burst-rate[/tx-burst-rate] 
	             * [rx-burst-threshold[/tx-burst-threshold] 
	             * [rx-burst-time[/tx-burst-time] [priority] 
	             * [rx-rate-min[/tx-rate-min]]]] 
	             * 其中，[]中内容表示可选。
	             * "rx" 表示客户的上传速率，
	             * "tx"客户的下载速率。
	             * burst-rate突发速率，
	             * burst-threshold突发门限，
	             * burst-time突发时长。
	            */
	            long up = user.getUpRate() * 1024;
	            long down = user.getDownRate() * 1024;
	            if(user.getUpPeakRate() !=null && user.getDownPeakRate() != null){
	            	dmreq.addAttribute("Mikrotik-Rate-Limit", String.format("%sk/%sk %sk/%sk", up,down,user.getUpPeakRate()*1024,user.getDownPeakRate()*1024));
	            }else{
	            	dmreq.addAttribute("Mikrotik-Rate-Limit", String.format("%sk/%sk", up,down));
	            }
	            // 添加登录人数限制
	            dmreq.addAttribute("Acct-Link-Count", String.valueOf(user.getActiveNum()));
	            
	            logger.info(online.getUsername(), "发送动态授权请求 " + dmreq.toLineString(), Memarylogger.RADIUSD_COA);
	            if(online.isRadsec()){
	                coaque.addLast(dmreq);
	            }
	            RadiusPacket dmrep = cli.communicate(dmreq,bras.getCoaPort());
	            logger.info(online.getUsername(), "接收到动态授权响应 " + dmrep.toLineString(), Memarylogger.RADIUSD_COA);
	        } catch (ServiceException | IOException | RadiusException e) {
	            logger.error(online.getUsername(),"发送动态授权失败",e, Memarylogger.RADIUSD_COA);
	        }
        });
    }

}
