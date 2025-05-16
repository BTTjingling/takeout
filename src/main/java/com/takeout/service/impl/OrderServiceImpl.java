package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.Order;
import com.takeout.mapper.OrderMapper;
import com.takeout.service.OrderService;
import org.springframework.stereotype.Service;

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
        return baseMapper.selectPage(page, queryWrapper);
    }
}