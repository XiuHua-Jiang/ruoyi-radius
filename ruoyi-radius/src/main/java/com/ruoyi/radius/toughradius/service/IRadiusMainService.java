package com.ruoyi.radius.toughradius.service;

import com.ruoyi.radius.toughradius.domain.RadiusMain;

/**
 * 首页统计Service接口
 *
 * @author panweilei
 * @date 2021-01-26
 */
public interface IRadiusMainService {
    /**
     * 查询首页统计信息
     * @return
     */
    public RadiusMain selectRadiusStatistics();
}
