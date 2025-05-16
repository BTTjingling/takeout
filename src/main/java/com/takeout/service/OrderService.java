package com.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.entity.Order;

public interface OrderService extends IService<Order> {
    /**
     * 分页获取所有订单信息
     * @param page 分页对象
     * @return 分页后的订单列表
     */
    Page<Order> getAllOrders(Page<Order> page);
    Page<Order> getOrdersByUserId(Long userId, Page<Order> page);
    Page<Order> getOrdersByShopId(Long shopId, Page<Order> page);
}