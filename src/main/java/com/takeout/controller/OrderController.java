package com.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.Order;
import com.takeout.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 管理员查看所有订单信息
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页后的订单列表
     */
    @GetMapping("/admin/orders")
    public Page<Order> getAllOrders(@RequestParam(defaultValue = "1") Long pageNum,
                                    @RequestParam(defaultValue = "10") Long pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        return orderService.getAllOrders(page);
    }
    @GetMapping("/api/orders/todayCount")
    public Integer getTodayOrderCount(@RequestParam Long shopId) {
        return orderService.getTodayOrderCount(shopId);
    }
}