package com.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.Merchant;
import com.takeout.entity.User;
import com.takeout.entity.Order;
import com.takeout.service.MerchantService;
import com.takeout.service.OrderService;
import com.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * 管理员查看所有用户信息
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页后的用户列表
     */
    @GetMapping("/users")
    public Page<User> getAllUsersForAdmin(@RequestParam(defaultValue = "1") Long pageNum,
                                          @RequestParam(defaultValue = "10") Long pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return userService.getAllUsers(page);
    }

    /**
     * 管理员查看具体用户的详细订单记录
     * @param userId 用户 ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页后的订单列表
     */

    @GetMapping("/users/{userId}/orders")
    public Page<Order> getOrdersByUserId(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "1") Long pageNum,
                                         @RequestParam(defaultValue = "10") Long pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        return orderService.getOrdersByUserId(userId, page);
    }

    /**
     * 管理员查看所有商家信息
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页后的商家列表
     */
    @GetMapping("/merchants")
    public Page<Merchant> getAllMerchantsForAdmin(@RequestParam(defaultValue = "1") Long pageNum,
                                                  @RequestParam(defaultValue = "10") Long pageSize) {
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        return merchantService.getAllMerchants(page);
    }
    /**
     * 管理员查看单个商家的订单信息
     * @param shopId 商家 ID
     * @param pageNum 页码，默认值为 1
     * @param pageSize 每页数量，默认值为 10
     * @return 分页后的订单列表
     */
    @GetMapping("/merchants/{shopId}/orders")
    public Page<Order> getOrdersByShopId(@PathVariable Long shopId,
                                         @RequestParam(defaultValue = "1") Long pageNum,
                                         @RequestParam(defaultValue = "10") Long pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        return orderService.getOrdersByShopId(shopId, page);
    }
}