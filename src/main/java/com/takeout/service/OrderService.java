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

    // 如果不需要无筛选版本，可以移除这个方法

    /**
     * 更新订单状态
     * @param orderId 订单 ID
     * @param newStatus 新的订单状态
     * @return 更新是否成功
     */
    boolean updateOrderStatus(Long orderId, String newStatus);
    Page<Order> getOrdersByShopId(Long shopId, Page<Order> page,
                                  String orderId, String userId, String status);
    /**
     * 用户取消订单
     * @param orderId 订单 ID
     * @return 取消是否成功
     */
    boolean cancelOrder(Long orderId);
}