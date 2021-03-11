package com.ruoyi.radius.toughradius.service;


import com.google.gson.Gson;
import com.ruoyi.radius.toughradius.common.FileUtil;
import com.ruoyi.radius.toughradius.config.Constant;
import com.ruoyi.radius.toughradius.config.RadiusConfig;
import com.ruoyi.radius.toughradius.domain.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Radius 定时任务设计
 */
@EnableScheduling
@Component
public class SystemTaskScheduler implements Constant {

    @Autowired
    private OnlineCache onlineCache;

    @Autowired
    private TicketCache ticketCache;

    @Autowired
    private SubscribeCache subscribeCache;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RadiusConfig radiusConfig;

    @Autowired
    private RadiusStat radiusStat;

    @Autowired
    private RadiusAuthStat radiusAuthStat;

    @Autowired
    private RadiusOnlineStat radiusOnlineStat;

    @Autowired
    private Gson gson;

    @Autowired
    private ThreadPoolTaskExecutor systaskExecutor;


    /**
     * 消息统计任务
     */
    @Scheduled(fixedRate = 5 * 1000, initialDelay = 5)
    public void syncStatFile(){
        systaskExecutor.execute(()->{
            radiusStat.runStat();
            FileUtil.writeFile(radiusConfig.getStatfile(),gson.toJson(radiusStat.getData()));
        });
    }

    /**
     * 消息统计任务
     */
    @Scheduled(fixedRate = 5 * 1000, initialDelay = 5)
    public void updateRadiusAuthStat(){
        systaskExecutor.execute(()->{
            radiusAuthStat.runStat();
        });
    }

    /**
     * 消息统计任务
     */
    @Scheduled(fixedRate = 5 * 1000, initialDelay = 5)
    public void updateRadiusOnlineStat(){
        systaskExecutor.execute(()->{
            radiusOnlineStat.runStat();
        });
    }

    /**
     * 在线用户清理
     */
    @Scheduled(fixedRate =300 * 1000)
    public void  checkOnlineExpire(){
        // update by panweilei 不从数据库配置中获取 直接设置10秒
        /*Config config = configService.findConfig(RADIUS_MODULE,RADIUS_INTERIM_INTELVAL);
        if(config!=null){
            int interim_times = Integer.valueOf(config.getValue());
            onlineCache.clearOvertimeTcRadiusOnline(interim_times);
        }*/
        //System.out.println("在线用户清理 定时器 执行 >>>>>>>>>>>");
        onlineCache.clearOvertimeTcRadiusOnline(10);
    }

    /**
     * 过期用户清理
     */
    @Scheduled(fixedRate =300 * 1000)
    public void  checkOnlineUserExpire(){
        //System.out.println("过期用户清理 定时器 执行 >>>>>>>>>>>");
        onlineCache.unlockExpireTcRadiusOnline();
    }


    /**
     * 更新用户缓存
     */
    @Scheduled(fixedRate = 10 * 1000)
    public void  updateSubscribeCache(){
        systaskExecutor.execute(() -> subscribeCache.updateSubscribeCache());
    }

    /**
     * 同步上网日志
     */
    @Scheduled(fixedRate = 10 * 1000)
    public void syncTcRadiusTicket() {
        systaskExecutor.execute(()->ticketCache.syncData());
    }

}
