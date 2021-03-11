package com.ruoyi.radius.toughradius.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.ruoyi.radius.toughradius.domain.Meal;

import java.util.List;

@Repository
@Mapper
public interface MealMapper {

    /**
     * 根据名称查询套餐
     * @param mealName
     * @return
     */
    Meal findMealByName(@Param("mealName") String mealName);

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
     * 删除套餐管理
     *
     * @param id 套餐管理ID
     * @return 结果
     */
    public int deleteMealById(Integer id);

    /**
     * 批量删除套餐管理
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteMealByIds(String[] ids);

}