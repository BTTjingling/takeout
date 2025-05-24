package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.Order;
import com.takeout.mapper.OrderMapper;
import com.takeout.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Override
    public Page<Order> getAllOrders(Page<Order> page) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("order_time");
        return baseMapper.selectPage(page, queryWrapper);
    }


    @Override
    public Page<Order> getOrdersByUserId(Long userId, Page<Order> page) {
        // 创建 QueryWrapper 对象并设置查询条件
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        // 调用 baseMapper 的 selectPage 方法进行分页查询
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Order> getOrdersByShopId(Long shopId, Page<Order> page) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", shopId);
        if (page == null) {
            // 如果 page 为 null，创建一个足够大的分页对象以获取所有数据
            page = new Page<>(1, Long.MAX_VALUE);
        }
        return baseMapper.selectPage(page, queryWrapper);
    }
    @Override
    public boolean updateOrderStatus(Long orderId, String newStatus) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id", orderId);
        updateWrapper.set("Ostatus", newStatus);
        return this.update(updateWrapper);
    }
    @Override
    public boolean cancelOrder(Long orderId) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id", orderId);
        updateWrapper.set("Ostatus", "用户已取消");
        return this.update(updateWrapper);
    }
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        // 实现根据用户 ID 获取所有订单的逻辑
        return baseMapper.selectList(null);
    }

}