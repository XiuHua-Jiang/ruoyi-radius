package com.ruoyi.radius.toughradius.service.impl;

import com.ruoyi.radius.toughradius.domain.RadiusMain;
import com.ruoyi.radius.toughradius.domain.RadiusOnline;
import com.ruoyi.radius.toughradius.mapper.RadiusMainMapper;
import com.ruoyi.radius.toughradius.service.IRadiusMainService;
import com.ruoyi.radius.toughradius.service.OnlineCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
/**
 * 统计信息Service业务层处理
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Service
public class RadiusMainServiceImpl implements IRadiusMainService {

    @Autowired
    private RadiusMainMapper radiusMainMapper;

    @Autowired
    private OnlineCache onlineCache;
    /**
     * 查询首页统计信息
     *
     * @return
     */
    @Override
    public RadiusMain selectRadiusStatistics() {

        List<RadiusOnline> list = onlineCache.getReadonlyOnlineList();
        Integer onLineTempUserCount = 0;
        Integer onLineRegisterUserCount = 0;
        Set set = new HashSet();
        for (RadiusOnline online : list){
            if(online.getUsername().contains("zchx")){
                onLineTempUserCount ++;
            }else {
                onLineRegisterUserCount ++;
            }
            set.add(online.getNasId());
        }
        RadiusMain radiusMain = radiusMainMapper.selectRadiusStatistics();
        radiusMain.setOnLineBrasCount(String.valueOf(set.size()));
        radiusMain.setOnLineTempUserCount(String.valueOf(onLineTempUserCount));
        radiusMain.setOnLineRegisterUserCount(String.valueOf(onLineRegisterUserCount));
        return radiusMain;
    }
}
