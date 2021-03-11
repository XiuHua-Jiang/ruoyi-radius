package com.ruoyi.radius.toughradius.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.radius.toughradius.form.OrderForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.radius.toughradius.mapper.OrderMapper;
import com.ruoyi.radius.toughradius.domain.Order;
import com.ruoyi.radius.toughradius.service.IOrderService;
import com.ruoyi.common.core.text.Convert;

/**
 * 订单管理Service业务层处理
 * 
 * @author panweilei
 * @date 2021-01-26
 */
@Service
public class OrderServiceImpl implements IOrderService 
{
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 查询订单管理
     * 
     * @param orderNo 订单管理ID
     * @return 订单管理
     */
    @Override
    public Order selectByOrderNo(String orderNo)
    {
        return orderMapper.selectByOrderNo(orderNo);
    }

    /**
     * 查询订单信息
     * @param openId
     * @return
     */
    @Override
    public Order selectByOpenId(String openId) {
        return orderMapper.selectByOpenId(openId);
    }

    /**
     * 根据查询条件查询订单信息
     *
     * @param orderForm 查询条件实体类
     * @return
     */
    @Override
    public List<Order> query(OrderForm orderForm) {
        return orderMapper.query(orderForm);
    }

    /**
     * 查询订单管理列表
     * 
     * @param order 订单管理
     * @return 订单管理
     */
    @Override
    public List<Order> selectOrderList(Order order)
    {
        return orderMapper.selectOrderList(order);
    }

    /**
     * 新增订单管理
     * 
     * @param order 订单管理
     * @return 结果
     */
    @Override
    public int insertOrder(Order order)
    {
        order.setCreateTime(DateUtils.getNowDate());
        return orderMapper.insertOrder(order);
    }

    /**
     * 修改订单管理
     * 
     * @param order 订单管理
     * @return 结果
     */
    @Override
    public int updateOrder(Order order)
    {
        order.setUpdateTime(DateUtils.getNowDate());
        return orderMapper.updateOrder(order);
    }

    /**
     * 删除订单管理对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteOrderByIds(String ids)
    {
        return orderMapper.deleteOrderByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除订单管理信息
     * 
     * @param orderNo 订单管理ID
     * @return 结果
     */
    @Override
    public int deleteOrderById(String orderNo)
    {
        return orderMapper.deleteOrderById(orderNo);
    }
}
