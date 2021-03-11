package com.ruoyi.radius.toughradius.service;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.radius.toughradius.common.CoderUtil;
import com.ruoyi.radius.toughradius.config.Constant;
import com.ruoyi.radius.toughradius.domain.Config;
import com.ruoyi.radius.toughradius.mapper.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService implements Constant {

    @Autowired
    private ConfigMapper configMapper;

    public Config findConfig(String module, String name){
        return configMapper.findConfig(module,name);
    }

    public String getStringValue(String module, String name){
        Config cfg = configMapper.findConfig(module,name);
        if(cfg!=null){
            return cfg.getValue();
        }
        return null;
    }

    public Integer getInterimTimes(){
        return configMapper.getInterimTimes();
    }

    public Integer getIsCheckPwd(){
        return configMapper.getIsCheckPwd();
    }

    public void insertConfig(Config config){
        configMapper.insertConfig(config);
    }

    public void updateConfig(Config config){
        Config cfg = configMapper.findConfig(config.getType(),config.getName());
        if(cfg==null){
            config.setId(CoderUtil.randomLong15Id());
            configMapper.insertConfig(config);
        }else{
            configMapper.updateConfig(config);
        }
    }

    public void deleteById(Integer id){
        configMapper.deleteById(id);
    }

    public void deleteConfig(@Param(value = "type") String type, @Param(value = "name") String name){
        configMapper.deleteConfig(type,name);
    }

    public List<Config> queryForList(@Param(value = "type") String type){
        return configMapper.queryForList(type);
    }

}
