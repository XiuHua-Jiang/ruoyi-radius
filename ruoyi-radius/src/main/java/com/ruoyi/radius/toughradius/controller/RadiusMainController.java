package com.ruoyi.radius.toughradius.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.radius.toughradius.service.IRadiusMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页 控制器
 */
@Controller
@RequestMapping("/radius")
public class RadiusMainController extends BaseController {

    private String prefix = "radius";

    @Autowired
    private IRadiusMainService radiusMainService;

    /**
     * 首页
     * @return
     */
    @GetMapping("main")
    public String main(ModelMap mmap)
    {
        mmap.put("radiusMain",radiusMainService.selectRadiusStatistics());
        return prefix + "/main";
    }
}
