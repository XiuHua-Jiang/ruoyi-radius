package com.ruoyi.radius.toughradius.service.impl;

import java.util.Date;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.radius.toughradius.mapper.SubscribeMapper;
import com.ruoyi.radius.toughradius.domain.Subscribe;
import com.ruoyi.radius.toughradius.service.ISubscribeService;
import com.ruoyi.common.core.text.Convert;

/**
 * 用户管理Service业务层处理
 * 
 * @author panweilei
 * @date 2021-01-25
 */
@Service
public class SubscribeServiceImpl implements ISubscribeService 
{
    @Autowired
    private SubscribeMapper subscribeMapper;

    /**
     * 查询用户管理
     * 
     * @param id 用户管理ID
     * @return 用户管理
     */
    @Override
    public Subscribe selectSubscribeById(Long id)
    {
        return subscribeMapper.selectSubscribeById(id);
    }

    /**
     * 查询用户管理列表
     * 
     * @param subscribe 用户管理
     * @return 用户管理
     */
    @Override
    public List<Subscribe> selectSubscribeList(Subscribe subscribe)
    {
        return subscribeMapper.selectSubscribeList(subscribe);
    }

    /**
     * 新增用户管理
     * 
     * @param subscribe 用户管理
     * @return 结果
     */
    @Override
    public int insertSubscribe(Subscribe subscribe)
    {
        subscribe.setCreateTime(DateUtils.getNowDate());
        subscribe.setBeginTime(DateUtils.getNowDate());
        // 密码和用户名一样
        subscribe.setPassword(subscribe.getSubscriber());
        // 状态更新，如果到期时间小于当前日期，且状态是正常，变为过期状态
        if(subscribe.getExpireTime().before(DateUtils.getNowDate()) && subscribe.getStatus().equals("enabled")){
            subscribe.setStatus("expire");
        }
        return subscribeMapper.insertSubscribe(subscribe);
    }

    /**
     * 修改用户管理
     * 
     * @param subscribe 用户管理
     * @return 结果
     */
    @Override
    public int updateSubscribe(Subscribe subscribe)
    {
        subscribe.setUpdateTime(DateUtils.getNowDate());
        // 状态更新，如果到期时间大于当前日期，且状态是过期，变为正常状态
        if(subscribe.getExpireTime().after(DateUtils.getNowDate()) && subscribe.getStatus().equals("expire")){
            subscribe.setStatus("enabled");
        }
        // 状态更新，如果到期时间小于当前日期，且状态是正常，变为过期状态
        if(subscribe.getExpireTime().before(DateUtils.getNowDate()) && subscribe.getStatus().equals("enabled")){
            subscribe.setStatus("expire");
        }
        return subscribeMapper.updateSubscribe(subscribe);
    }

    /**
     * 删除用户管理对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteSubscribeByIds(String ids)
    {
        return subscribeMapper.deleteSubscribeByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除用户管理信息
     * 
     * @param id 用户管理ID
     * @return 结果
     */
    @Override
    public int deleteSubscribeById(Long id)
    {
        return subscribeMapper.deleteSubscribeById(id);
    }

    @Override
    public Subscribe findSubscribe(String username){
        return subscribeMapper.findSubscribe(username);
    }
    @Override
    public Integer updateMacAddr(String username, String macAddr){
        return subscribeMapper.updateMacAddr(username,macAddr);
    }
    @Override
    public Integer updateInValn(String username, Integer inValn){
        return subscribeMapper.updateInValn(username,inValn);
    }
    @Override
    public Integer updateOutValn(String username, Integer outValn){
        return subscribeMapper.updateOutValn(username,outValn);
    }
    @Override
    public List<Subscribe> findLastUpdateUser(String lastUpdate) {
        return subscribeMapper.findLastUpdateUser(lastUpdate);
    }
    @Override
    public List<Subscribe> findByMacAddr(String macAddr, Date lastTime){
        return subscribeMapper.findByMacAddr(macAddr,lastTime);
    }
    @Override
    public void release(String ids){
        subscribeMapper.release(ids);
    }
}
