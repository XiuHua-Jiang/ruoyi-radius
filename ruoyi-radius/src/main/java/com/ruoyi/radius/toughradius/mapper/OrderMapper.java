package com.ruoyi.radius.toughradius.mapper;

import java.util.List;
import com.ruoyi.radius.toughradius.domain.Order;
import com.ruoyi.radius.toughradius.form.OrderForm;
import org.springframework.stereotype.Component;

/**
 * 订单管理Mapper接口
 *
 * @author panweilei
 * @date 2021-01-26
 */
@Component
public interface OrderMapper
{
	/**
	 * 根据条件查询订单信息
	 * @param orderForm
	 * @return
	 */
	List<Order> query(OrderForm orderForm);
	/**
	 * 查询订单管理
	 *
	 * @param orderNo 订单管理ID
	 * @return 订单管理
	 */
	public Order selectByOrderNo(String orderNo);

	/**
	 * 查询订单管理
	 * @param openId
	 * @return
	 */
	public Order selectByOpenId(String openId);
	/**
	 * 查询订单管理列表
	 *
	 * @param order 订单管理
	 * @return 订单管理集合
	 */
	public List<Order> selectOrderList(Order order);

	/**
	 * 新增订单管理
	 *
	 * @param order 订单管理
	 * @return 结果
	 */
	public int insertOrder(Order order);

	/**
	 * 修改订单管理
	 *
	 * @param order 订单管理
	 * @return 结果
	 */
	public int updateOrder(Order order);

	/**
	 * 删除订单管理
	 *
	 * @param orderNo 订单管理ID
	 * @return 结果
	 */
	public int deleteOrderById(String orderNo);

	/**
	 * 批量删除订单管理
	 *
	 * @param orderNos 需要删除的数据ID
	 * @return 结果
	 */
	public int deleteOrderByIds(String[] orderNos);


}
