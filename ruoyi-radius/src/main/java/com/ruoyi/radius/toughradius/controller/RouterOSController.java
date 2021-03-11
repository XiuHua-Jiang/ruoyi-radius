package com.ruoyi.radius.toughradius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.ruoyi.radius.toughradius.common.ValidateUtil;
import com.ruoyi.radius.toughradius.service.ConfigService;
import com.ruoyi.radius.toughradius.service.Memarylogger;
import com.ruoyi.radius.toughradius.config.Constant;
import com.ruoyi.radius.toughradius.form.RouterOSParam;
import com.ruoyi.radius.toughradius.form.WlanParam;

import javax.servlet.http.HttpServletRequest;
/**
 * RouterOS
 * （注：原ToughRADIUS中的类 暂时未使用）
 *
 * @author panweilei
 * @date 2021-01-25
 */
@Controller
public class RouterOSController implements  Constant{

    @Autowired
    private ConfigService configService;

    @Autowired
    protected Memarylogger logger;


    private String getWlanemplate(){
        String template = configService.getStringValue(WLAN_MODULE,WLAN_TEMPLATE);
        if(ValidateUtil.isEmpty(template)){
            return "default";
        }
        return template;
    }

    /**
     * portal 首页
     * @param rosParam
     * @return
     */
    @RequestMapping("/routeros/login")
    public ModelAndView wlanIndexHandler(RouterOSParam rosParam, HttpServletRequest request){
        logger.info(rosParam.getUsername(),"接收到RouterOS 认证请求 "+rosParam.toString(),Memarylogger.PORTAL);
        String joinUrl = configService.getStringValue(WLAN_MODULE, WLAN_JOIN_URL);
        String template = getWlanemplate();
        ModelAndView modelAndView = new ModelAndView(template+"/roslogin");
        modelAndView.addObject("param", rosParam);
        modelAndView.addObject("joinUrl", joinUrl);
        return modelAndView;
    }


    /**
     * portal 首页
     * @param rosParam
     * @return
     */
    @GetMapping("/routeros/status")
    public ModelAndView wlanStatusHandler(RouterOSParam rosParam, HttpServletRequest request){
        logger.info(rosParam.getUsername(),"RouterOS 认证状态 "+rosParam.toString(),Memarylogger.PORTAL);
        String resultUrl = configService.getStringValue(WLAN_MODULE, Constant.WLAN_RESULT_URL);
        if(ValidateUtil.isEmpty(resultUrl)){
            resultUrl = "http://www.baidu.com";
        }
        String template = getWlanemplate();
        ModelAndView modelAndView = new ModelAndView(template+"/rosstatus");
        modelAndView.addObject("param", rosParam);
        modelAndView.addObject("resultUrl", resultUrl);
        return modelAndView;
    }




}
