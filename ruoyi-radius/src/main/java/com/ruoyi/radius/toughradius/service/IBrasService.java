package com.ruoyi.radius.toughradius.service;

import java.util.List;
import com.ruoyi.radius.toughradius.domain.Bras;

/**
 * 设备管理Service接口
 * 
 * @author panweilei
 * @date 2021-01-25
 */
public interface IBrasService 
{
    /**
     * 查询设备管理
     * 
     * @param id 设备管理ID
     * @return 设备管理
     */
    public Bras selectBrasById(Long id);

    /**
     * 查询设备管理列表
     * 
     * @param bras 设备管理
     * @return 设备管理集合
     */
    public List<Bras> selectBrasList(Bras bras);

    /**
     * 新增设备管理
     * 
     * @param bras 设备管理
     * @return 结果
     */
    public int insertBras(Bras bras);

    /**
     * 修改设备管理
     * 
     * @param bras 设备管理
     * @return 结果
     */
    public int updateBras(Bras bras);

    /**
     * 批量删除设备管理
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBrasByIds(String ids);

    /**
     * 删除设备管理信息
     * 
     * @param id 设备管理ID
     * @return 结果
     */
    public int deleteBrasById(Long id);

    /**
     * 查找 BRAS 信息
     * @param ipaddr 数据包来源IP
     * @param srcip 设备中配置的IP，可能是内网IP
     * @param identifier  设备唯一标识
     * @return
     * @throws ServiceException
     */
    public Bras findBras(String ipaddr, String srcip, String identifier) throws ServiceException;

    /**
     * 校验唯一标识
     * @param bras
     * @return
     */
    public String checkIdentifierUnique(Bras bras);
}
