package com.ruoyi.radius.toughradius.controller;

import com.ruoyi.radius.toughradius.common.PageResult;
import com.ruoyi.radius.toughradius.domain.TraceMessage;
import com.ruoyi.radius.toughradius.service.Memarylogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * 系统日志
 * （注：原ToughRADIUS中的类 暂时未使用）
 *
 * @author panweilei
 * @date 2021-01-25
 */
@RestController
public class SyslogController {

    @Autowired
    private Memarylogger logger;

    @GetMapping({"/api/v6/syslog/query","/admin/syslog/query"})
    public PageResult<TraceMessage> queryTraceMessage(@RequestParam(defaultValue = "0") int start, @RequestParam(defaultValue = "40") int count,
                                                      String startDate, String endDate, String type, String username, String keyword){
        return logger.queryMessage(start,count,startDate,endDate,type, username,keyword);
    }

}
