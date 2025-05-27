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
import org.springframework.http.ResponseEntity;
import java.util.Map; // 添加这个导入
import org.springframework.web.bind.annotation.CrossOrigin;
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

    @GetMapping("/merchants/{shopId}/orders")
    public Page<Order> getOrdersByShopId(@PathVariable Long shopId,
                                         @RequestParam(defaultValue = "1") Long pageNum,
                                         @RequestParam(defaultValue = "10") Long pageSize,
                                         @RequestParam(required = false) String orderId,
                                         @RequestParam(required = false) String userId,
                                         @RequestParam(required = false) String status) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        return orderService.getOrdersByShopId(shopId, page, orderId, userId, status);
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


    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> request) {
        String newStatus = request.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().body("缺少状态参数");
        }

        // 验证状态值是否合法
        if (!Order.ALLOWED_STATUSES.contains(newStatus)) {
            return ResponseEntity.badRequest().body("无效的订单状态");
        }

        boolean success = orderService.updateOrderStatus(orderId, newStatus);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    }

