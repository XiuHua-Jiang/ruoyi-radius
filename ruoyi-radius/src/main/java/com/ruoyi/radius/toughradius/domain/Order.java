package com.ruoyi.radius.toughradius.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单管理对象 com_order
 *
 * @author panweilei
 * @date 2021-01-26
 */
public class Order extends BaseEntity
{
	private static final long serialVersionUID = 1L;

	/** 系统生成的订单号 */
	@Excel(name = "系统生成的订单号")
	private String orderNo;

	/** 客户信息：手机号 */
	@Excel(name = "客户信息：手机号")
	private String customer;

	/** 支付类型 1 支付宝 2 微信 */
	@Excel(name = "支付类型")
	private Integer payType;

	/** 第三方订单号 */
	@Excel(name = "第三方订单号")
	private String openId;

	/** 套餐ID */
	private Integer mealId;

	/** 套餐名称 */
	@Excel(name = "套餐名称")
	private String mealName;

	/** 订单金额 */
	@Excel(name = "订单金额")
	private Integer money;

	/** 订单状态 0 待支付 1 支付成功 2 支付失败 */
	@Excel(name = "订单状态 0 待支付 1 支付成功 2 支付失败")
	private Integer status;

	/** 临时账号 */
	@Excel(name = "临时账号")
	private String tempUserName;

	public void setOrderNo(String orderNo)
	{
		this.orderNo = orderNo;
	}

	public String getOrderNo()
	{
		return orderNo;
	}
	public void setCustomer(String customer)
	{
		this.customer = customer;
	}

	public String getCustomer()
	{
		return customer;
	}
	public void setPayType(Integer payType)
	{
		this.payType = payType;
	}

	public Integer getPayType()
	{
		return payType;
	}
	public void setOpenId(String openId)
	{
		this.openId = openId;
	}

	public String getOpenId()
	{
		return openId;
	}
	public void setMealId(Integer mealId)
	{
		this.mealId = mealId;
	}

	public Integer getMealId()
	{
		return mealId;
	}
	public void setMoney(Integer money)
	{
		this.money = money;
	}

	public Integer getMoney()
	{
		return money;
	}
	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public Integer getStatus()
	{
		return status;
	}
	public void setTempUserName(String tempUserName)
	{
		this.tempUserName = tempUserName;
	}

	public String getTempUserName()
	{
		return tempUserName;
	}

	public String getMealName() {
		return mealName;
	}

	public void setMealName(String mealName) {
		this.mealName = mealName;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
				.append("orderNo", getOrderNo())
				.append("customer", getCustomer())
				.append("payType", getPayType())
				.append("openId", getOpenId())
				.append("mealId", getMealId())
				.append("mealName", getMealName())
				.append("money", getMoney())
				.append("status", getStatus())
				.append("createTime", getCreateTime())
				.append("updateTime", getUpdateTime())
				.append("tempUserName", getTempUserName())
				.toString();
	}
}
