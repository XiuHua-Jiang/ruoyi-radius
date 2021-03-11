package com.ruoyi.radius.toughradius.domain;


import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 套餐管理对象 com_meal
 *
 * @author panweilei
 * @date 2021-01-22
 */
public class Meal extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 套餐名称 */
    @Excel(name = "套餐名称")
    private String mealName;

    /** 套餐使用人数限制数 */
    @Excel(name = "套餐使用人数限制数")
    private Integer useCount;

    /** 套餐周期（单位：小时） */
    @Excel(name = "套餐周期", readConverterExp = "单=位：小时")
    private Integer useHour;

    /** 套餐金额：单位（分） */
    @Excel(name = "套餐金额：单位", readConverterExp = "分=")
    private Integer price;

    /** 是否启用：0 否 1 是 */
    @Excel(name = "是否启用：0 否 1 是")
    private Integer enable;

    /** 排序，1大于2排前面 */
    @Excel(name = "排序，1大于2排前面")
    private Integer sortNumber;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setMealName(String mealName)
    {
        this.mealName = mealName;
    }

    public String getMealName()
    {
        return mealName;
    }
    public void setUseCount(Integer useCount)
    {
        this.useCount = useCount;
    }

    public Integer getUseCount()
    {
        return useCount;
    }
    public void setUseHour(Integer useHour)
    {
        this.useHour = useHour;
    }

    public Integer getUseHour()
    {
        return useHour;
    }
    public void setPrice(Integer price)
    {
        this.price = price;
    }

    public Integer getPrice()
    {
        return price;
    }
    public void setEnable(Integer enable)
    {
        this.enable = enable;
    }

    public Integer getEnable()
    {
        return enable;
    }
    public void setSortNumber(Integer sortNumber)
    {
        this.sortNumber = sortNumber;
    }

    public Integer getSortNumber()
    {
        return sortNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mealName", getMealName())
                .append("useCount", getUseCount())
                .append("useHour", getUseHour())
                .append("price", getPrice())
                .append("enable", getEnable())
                .append("sortNumber", getSortNumber())
                .append("remark", getRemark())
                .toString();
    }
}