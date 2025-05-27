package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.Order;
import com.takeout.mapper.OrderMapper;
import com.takeout.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
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
    @Override
    public Integer getTodayOrderCount(Long shopId) {
        return orderMapper.countTodayOrdersByShopId(shopId, LocalDate.now());
    }
    @Override
    public Double getTodayRevenue(Long shopId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("SUM(total_amount) as total")
                .eq("shop_id", shopId)
                .ge("order_time", LocalDate.now().atStartOfDay())
                .le("order_time", LocalDate.now().atTime(23, 59, 59));

        Map<String, Object> result = getMap(queryWrapper);
        if (result != null && result.get("total") != null) {
            return ((BigDecimal) result.get("total")).doubleValue();
        }
        return 0.0;
    }
    @Override
    public long countPendingOrders(Long shopId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", shopId)
                .in("Ostatus", Arrays.asList("未接单", "已接单制作中", "配送中"));
        return this.count(queryWrapper);
    }
    @Override
    public Map<String, Double> getRevenueLast7Days(Long shopId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DATE_FORMAT(order_time, '%Y-%m-%d') as date, SUM(total_amount) as amount")
                .eq("shop_id", shopId)
                .between("order_time", startDate.atStartOfDay(), endDate.atTime(23, 59, 59))
                .groupBy("DATE_FORMAT(order_time, '%Y-%m-%d')")
                .orderByAsc("date"); // 修改这里，直接使用别名排序

        List<Map<String, Object>> result = baseMapper.selectMaps(queryWrapper);

        Map<String, Double> revenueMap = new LinkedHashMap<>();
        for (Map<String, Object> item : result) {
            revenueMap.put(
                    item.get("date").toString(),
                    ((Number)item.get("amount")).doubleValue()
            );
        }

        // 确保返回7天的数据，没有数据的日期填充0.0
        for (int i = 0; i < 7; i++) {
            String date = startDate.plusDays(i).toString();
            revenueMap.putIfAbsent(date, 0.0);
        }
        return revenueMap;
    }
}