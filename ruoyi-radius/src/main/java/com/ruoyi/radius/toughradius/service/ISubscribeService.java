package com.ruoyi.radius.toughradius.service;

import java.util.Date;
import java.util.List;
import com.ruoyi.radius.toughradius.domain.Subscribe;

/**
 * 用户管理Service接口
 * 
 * @author panweilei
 * @date 2021-01-25
 */
public interface ISubscribeService 
{
    /**
     * 查询用户管理
     * 
     * @param id 用户管理ID
     * @return 用户管理
     */
    public Subscribe selectSubscribeById(Long id);

    /**
     * 查询用户管理列表
     * 
     * @param subscribe 用户管理
     * @return 用户管理集合
     */
    public List<Subscribe> selectSubscribeList(Subscribe subscribe);

    /**
     * 新增用户管理
     * 
     * @param subscribe 用户管理
     * @return 结果
     */
    public int insertSubscribe(Subscribe subscribe);

    /**
     * 修改用户管理
     * 
     * @param subscribe 用户管理
     * @return 结果
     */
    public int updateSubscribe(Subscribe subscribe);

    /**
     * 批量删除用户管理
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSubscribeByIds(String ids);

    /**
     * 删除用户管理信息
     * 
     * @param id 用户管理ID
     * @return 结果
     */
    public int deleteSubscribeById(Long id);

    public Subscribe findSubscribe(String username);

    public Integer updateMacAddr(String username, String macAddr);

    public Integer updateInValn(String username, Integer inValn);

    public Integer updateOutValn(String username, Integer outValn);

    public List<Subscribe> findLastUpdateUser(String lastUpdate);

    public List<Subscribe> findByMacAddr(String macAddr, Date lastTime);

    public void release(String ids);
}
