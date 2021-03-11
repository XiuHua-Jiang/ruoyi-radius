package com.ruoyi.radius.toughradius.controller;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysUser;
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
import com.ruoyi.radius.toughradius.domain.Bras;
import com.ruoyi.radius.toughradius.service.IBrasService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备管理Controller
 * 
 * @author panweilei
 * @date 2021-01-25
 */
@Controller
@RequestMapping("/radius/bras")
public class BrasController extends BaseController
{
    private String prefix = "radius/bras";

    @Autowired
    private IBrasService brasService;

    @RequiresPermissions("radius:bras:view")
    @GetMapping()
    public String bras()
    {
        return prefix + "/bras";
    }

    /**
     * 查询设备管理列表
     */
    @RequiresPermissions("radius:bras:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Bras bras)
    {
        startPage();
        List<Bras> list = brasService.selectBrasList(bras);
        return getDataTable(list);
    }

    /**
     * 导出设备管理列表
     */
    @RequiresPermissions("radius:bras:export")
    @Log(title = "设备管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Bras bras)
    {
        List<Bras> list = brasService.selectBrasList(bras);
        ExcelUtil<Bras> util = new ExcelUtil<Bras>(Bras.class);
        return util.exportExcel(list, "bras");
    }

    /**
     * 新增设备管理
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存设备管理
     */
    @RequiresPermissions("radius:bras:add")
    @Log(title = "设备管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Bras bras)
    {
        return toAjax(brasService.insertBras(bras));
    }

    /**
     * 修改设备管理
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        Bras bras = brasService.selectBrasById(id);
        mmap.put("bras", bras);
        return prefix + "/edit";
    }

    /**
     * 修改保存设备管理
     */
    @RequiresPermissions("radius:bras:edit")
    @Log(title = "设备管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Bras bras)
    {
        return toAjax(brasService.updateBras(bras));
    }

    /**
     * 删除设备管理
     */
    @RequiresPermissions("radius:bras:remove")
    @Log(title = "设备管理", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(brasService.deleteBrasByIds(ids));
    }


    /**
     * 校验唯一标识
     */
    @PostMapping("/checkIdentifierUnique")
    @ResponseBody
    public String checkIdentifierUnique(Bras bras)
    {
        return brasService.checkIdentifierUnique(bras);
    }
}
