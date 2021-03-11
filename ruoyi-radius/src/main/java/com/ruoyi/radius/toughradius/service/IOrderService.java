package com.ruoyi.radius.toughradius.service;

import java.util.List;
import com.ruoyi.radius.toughradius.domain.Order;
import com.ruoyi.radius.toughradius.form.OrderForm;

/**
 * 订单管理Service接口
 * 
 * @author panweilei
 * @date 2021-01-26
 */
public interface IOrderService 
{
    /**
     * 查询订单管理
     * 
     * @param orderNo 订单管理ID
     * @return 订单管理
     */
    public Order selectByOrderNo(String orderNo);
    /**
     * 查询订单信息
     * @param openId
     * @return
     */
    public Order selectByOpenId(String openId);

    /**
     * 根据查询条件查询订单信息
     * @param orderForm 查询条件实体类
     * @return
     */
    public List<Order> query(OrderForm orderForm);

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
     * 批量删除订单管理
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderByIds(String ids);

    /**
     * 删除订单管理信息
     * 
     * @param orderNo 订单管理ID
     * @return 结果
     */
    public int deleteOrderById(String orderNo);
}
