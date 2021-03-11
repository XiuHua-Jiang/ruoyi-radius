package com.ruoyi.radius.toughradius.service.impl;

import java.util.List;

import com.ruoyi.radius.toughradius.domain.Meal;
import com.ruoyi.radius.toughradius.mapper.MealMapper;
import com.ruoyi.radius.toughradius.service.IMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.text.Convert;

/**
 * 套餐管理Service业务层处理
 * 
 * @author panweilei
 * @date 2021-01-22
 */
@Service
public class MealServiceImpl implements IMealService
{
    @Autowired
    private MealMapper mealMapper;

    /**
     * 查询套餐管理
     * 
     * @param id 套餐管理ID
     * @return 套餐管理
     */
    @Override
    public Meal selectMealById(Integer id)
    {
        return mealMapper.selectMealById(id);
    }

    /**
     * 查询套餐管理列表
     * 
     * @param meal 套餐管理
     * @return 套餐管理
     */
    @Override
    public List<Meal> selectMealList(Meal meal)
    {
        return mealMapper.selectMealList(meal);
    }

    /**
     * 新增套餐管理
     * 
     * @param meal 套餐管理
     * @return 结果
     */
    @Override
    public int insertMeal(Meal meal)
    {
        return mealMapper.insertMeal(meal);
    }

    /**
     * 修改套餐管理
     * 
     * @param meal 套餐管理
     * @return 结果
     */
    @Override
    public int updateMeal(Meal meal)
    {
        return mealMapper.updateMeal(meal);
    }

    /**
     * 删除套餐管理对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteMealByIds(String ids)
    {
        return mealMapper.deleteMealByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除套餐管理信息
     * 
     * @param id 套餐管理ID
     * @return 结果
     */
    @Override
    public int deleteMealById(Integer id)
    {
        return mealMapper.deleteMealById(id);
    }

    /**
     * 根据名称查询套餐
     *
     * @param mealName
     * @return
     */
    @Override
    public Meal findMealByName(String mealName) {
        return mealMapper.findMealByName(mealName);
    }
}
