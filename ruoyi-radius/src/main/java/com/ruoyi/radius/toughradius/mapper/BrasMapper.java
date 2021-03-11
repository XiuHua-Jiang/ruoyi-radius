package com.ruoyi.radius.toughradius.mapper;

import java.util.List;
import com.ruoyi.radius.toughradius.domain.Bras;
import org.springframework.stereotype.Component;

/**
 * 设备管理Mapper接口
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Component
public interface BrasMapper
{
    /**
     * 根据标识查询
     * @param identifier
     * @return
     */
    public Bras findByidentifier(String identifier);

    /**
     * 根据IP地址查询
     * @param ipaddr
     * @return
     */
    public Bras findByIPAddr(String ipaddr);
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
     * 删除设备管理
     *
     * @param id 设备管理ID
     * @return 结果
     */
    public int deleteBrasById(Long id);

    /**
     * 批量删除设备管理
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBrasByIds(String[] ids);
}
