package com.ruoyi.radius.toughradius.controller;

import com.ruoyi.radius.toughradius.service.RadiusTester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 认证测试
 * （注：原ToughRADIUS中的类 暂时未使用）
 *
 * @author panweilei
 * @date 2021-01-25
 */
@RestController
public class TesterController {

    @Autowired
    private RadiusTester radiusTester;


    @GetMapping("/admin/radius/auth/test")
    public String AuthTestHandler(String username,String papchap){
        return radiusTester.sendAuth(username,papchap).replaceAll("\n","<br>");
    }


    @GetMapping("/admin/radius/acct/test")
    public String AcctTestHandler(String username,int type){
        return radiusTester.sendAcct(username,type).replaceAll("\n","<br>");
    }

}
