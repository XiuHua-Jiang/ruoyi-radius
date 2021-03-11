package com.ruoyi.radius.toughradius.controller;

import java.util.List;

import com.ruoyi.radius.toughradius.domain.Meal;
import com.ruoyi.radius.toughradius.service.IMealService;
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
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 套餐管理Controller
 * 
 * @author panweilei
 * @date 2021-01-22
 */
@Controller
@RequestMapping("/radius/meal")
public class MealController extends BaseController
{
    private String prefix = "radius/meal";

    @Autowired
    private IMealService mealService;

    @RequiresPermissions("radius:meal:view")
    @GetMapping()
    public String meal()
    {
        return prefix + "/meal";
    }

    /**
     * 查询套餐管理列表
     */
    @RequiresPermissions("radius:meal:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Meal meal)
    {
        startPage();
        List<Meal> list = mealService.selectMealList(meal);
        return getDataTable(list);
    }

    /**
     * 导出套餐管理列表
     */
    @RequiresPermissions("radius:meal:export")
    @Log(title = "套餐管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Meal meal)
    {
        List<Meal> list = mealService.selectMealList(meal);
        ExcelUtil<Meal> util = new ExcelUtil<Meal>(Meal.class);
        return util.exportExcel(list, "meal");
    }

    /**
     * 新增套餐管理
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存套餐管理
     */
    @RequiresPermissions("radius:meal:add")
    @Log(title = "套餐管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Meal meal)
    {
        return toAjax(mealService.insertMeal(meal));
    }

    /**
     * 修改套餐管理
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap)
    {
        Meal meal = mealService.selectMealById(id);
        mmap.put("meal", meal);
        return prefix + "/edit";
    }

    /**
     * 修改保存套餐管理
     */
    @RequiresPermissions("radius:meal:edit")
    @Log(title = "套餐管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Meal meal)
    {
        return toAjax(mealService.updateMeal(meal));
    }

    /**
     * 删除套餐管理
     */
    @RequiresPermissions("radius:meal:remove")
    @Log(title = "套餐管理", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(mealService.deleteMealByIds(ids));
    }
}
