package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.Order;
import com.takeout.mapper.OrderMapper;
import com.takeout.service.OrderService;
import org.springframework.stereotype.Service;

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
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("order_time");
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Order> getOrdersByShopId(Long shopId, Page<Order> page,
                                         String orderId, String userId, String status) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>()
                .eq("shop_id", shopId);

        if (orderId != null && !orderId.isEmpty()) {
            queryWrapper.eq("order_id", orderId);
        }
        if (userId != null && !userId.isEmpty()) {
            queryWrapper.eq("user_id", userId);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("ostatus", status);
        }

        return baseMapper.selectPage(page, queryWrapper);
    }

    // 如果不需要无筛选版本，可以移除这个方法


    @Override
    public boolean updateOrderStatus(Long orderId, String newStatus) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id", orderId);
        // 使用与数据库一致的Ostatus
        updateWrapper.set("Ostatus", newStatus);
        return this.update(updateWrapper);
    }

    @Override
    public boolean cancelOrder(Long orderId) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id", orderId);
        updateWrapper.set("ostatus", "用户已取消");
        return this.update(updateWrapper);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return null;
    }
}