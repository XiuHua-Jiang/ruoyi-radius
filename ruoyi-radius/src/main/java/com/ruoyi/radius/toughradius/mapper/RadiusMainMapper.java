package com.ruoyi.radius.toughradius.mapper;

import com.ruoyi.radius.toughradius.domain.RadiusMain;
import org.springframework.stereotype.Component;

/**
 * 首页统计 Mapper接口
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Component
public interface RadiusMainMapper {
    /**
     * 查询首页统计信息
     * @return
     */
    public RadiusMain selectRadiusStatistics();
}
