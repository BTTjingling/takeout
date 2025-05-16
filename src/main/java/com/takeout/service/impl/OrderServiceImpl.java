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
}