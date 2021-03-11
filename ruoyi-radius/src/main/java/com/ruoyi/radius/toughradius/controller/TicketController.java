package com.ruoyi.radius.toughradius.controller;

import com.ruoyi.radius.toughradius.common.PageResult;
import com.ruoyi.radius.toughradius.service.TicketCache;
import com.ruoyi.radius.toughradius.domain.RadiusTicket;
import com.ruoyi.radius.toughradius.service.Memarylogger;
import com.ruoyi.radius.toughradius.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
/**
 * 上网日志
 * （注：原ToughRADIUS中的类 暂时未使用）
 *
 * @author panweilei
 * @date 2021-01-25
 */
@RestController
public class TicketController {

    @Autowired
    private TicketCache ticketCache;

    @Autowired
    private Memarylogger logger;

    @GetMapping({"/api/v6/ticket/query","/admin/ticket/query"})
    public PageResult<RadiusTicket> queryTicket(@RequestParam(defaultValue = "0") int start,
                                                @RequestParam(defaultValue = "40") int count,
                                                String startDate,
                                                String endDate,
                                                String nasid,
                                                String nasaddr,
                                                Integer nodeId,
                                                String username,
                                                String keyword){

        try {
            return ticketCache.queryTicket(start,count,startDate,endDate, nasid, nasaddr, nodeId, username,keyword);
        } catch (ServiceException e) {
            logger.error(String.format("/admin查询上网日志发生错误, %s", e.getMessage()),e, Memarylogger.SYSTEM);
            return new PageResult<RadiusTicket>(start,0, new ArrayList<RadiusTicket>());
        }
    }
}
