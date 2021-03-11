package com.ruoyi.radius.toughradius.service;

import com.ruoyi.radius.toughradius.domain.Meal;

import java.util.List;

/**
 * 套餐管理Service接口
 * 
 * @author panweilei
 * @date 2021-01-22
 */
public interface IMealService 
{
    /**
     * 查询套餐管理
     * 
     * @param id 套餐管理ID
     * @return 套餐管理
     */
    public Meal selectMealById(Integer id);

    /**
     * 查询套餐管理列表
     * 
     * @param meal 套餐管理
     * @return 套餐管理集合
     */
    public List<Meal> selectMealList(Meal meal);

    /**
     * 新增套餐管理
     * 
     * @param meal 套餐管理
     * @return 结果
     */
    public int insertMeal(Meal meal);

    /**
     * 修改套餐管理
     * 
     * @param meal 套餐管理
     * @return 结果
     */
    public int updateMeal(Meal meal);

    /**
     * 批量删除套餐管理
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteMealByIds(String ids);

    /**
     * 删除套餐管理信息
     * 
     * @param id 套餐管理ID
     * @return 结果
     */
    public int deleteMealById(Integer id);

    /**
     * 根据名称查询套餐
     * @param mealName
     * @return
     */
    public Meal findMealByName(String mealName);

}
