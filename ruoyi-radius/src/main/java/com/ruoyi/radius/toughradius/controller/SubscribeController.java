package com.ruoyi.radius.toughradius.controller;

import java.util.List;

import com.ruoyi.radius.toughradius.common.DateTimeUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.radius.toughradius.domain.Subscribe;
import com.ruoyi.radius.toughradius.service.ISubscribeService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户管理Controller
 * 
 * @author panweilei
 * @date 2021-01-25
 */
@Controller
@RequestMapping("/radius/subscribe")
public class SubscribeController extends BaseController
{
    private String prefix = "radius/subscribe";

    @Autowired
    private ISubscribeService subscribeService;

    @RequiresPermissions("radius:subscribe:view")
    @GetMapping()
    public String subscribe()
    {
        return prefix + "/subscribe";
    }

    /**
     * 查询用户管理列表
     */
    @RequiresPermissions("radius:subscribe:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Subscribe subscribe)
    {
        startPage();
        List<Subscribe> list = subscribeService.selectSubscribeList(subscribe);
        TableDataInfo info = getDataTable(list);
        // 同步一下过期状态
        List<Subscribe> list2 = (List<Subscribe>)info.getRows();
        list2.forEach(item ->{
            if(item.getStatus().equals("enabled") && item.getExpireTime().before(DateTimeUtil.nowDate()))
                item.setStatus("expire");
        });
        info.setRows(list2);
        return info;
    }

    /**
     * 导出用户管理列表
     */
    @RequiresPermissions("radius:subscribe:export")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Subscribe subscribe)
    {
        List<Subscribe> list = subscribeService.selectSubscribeList(subscribe);
        ExcelUtil<Subscribe> util = new ExcelUtil<Subscribe>(Subscribe.class);
        return util.exportExcel(list, "subscribe");
    }

    /**
     * 新增用户管理
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存用户管理
     */
    @RequiresPermissions("radius:subscribe:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Subscribe subscribe)
    {
        return toAjax(subscribeService.insertSubscribe(subscribe));
    }

    /**
     * 修改用户管理
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        Subscribe subscribe = subscribeService.selectSubscribeById(id);
        mmap.put("subscribe", subscribe);
        return prefix + "/edit";
    }

    /**
     * 修改保存用户管理
     */
    @RequiresPermissions("radius:subscribe:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Subscribe subscribe)
    {
        return toAjax(subscribeService.updateSubscribe(subscribe));
    }

    /**
     * 删除用户管理
     */
    @RequiresPermissions("radius:subscribe:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(subscribeService.deleteSubscribeByIds(ids));
    }
}
