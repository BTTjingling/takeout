package com.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.Merchant;
import com.takeout.entity.User;
import com.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.takeout.service.MerchantService;
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
}