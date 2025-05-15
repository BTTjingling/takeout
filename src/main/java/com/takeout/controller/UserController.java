package com.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.Merchant;
import com.takeout.entity.User;
import com.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.takeout.service.MerchantService;
import com.takeout.entity.Order;
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserService userService;
    @GetMapping("/info")
    public User getUserInfo(@RequestParam Long userId) {
        return userService.getUserInfo(userId);
    }
    @PostMapping("/submitOrder")
    public String submitOrder(@RequestBody Order order) {
        return userService.submitOrder(order);
    }
    @GetMapping("/admin/all")
    public Page<User> getAllUsersForAdmin(@RequestParam(defaultValue = "1") Long pageNum,
                                          @RequestParam(defaultValue = "10") Long pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return userService.getAllUsers(page);
    }

}