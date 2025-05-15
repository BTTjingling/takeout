package com.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.Merchant;
import com.takeout.entity.User;
import com.takeout.service.MerchantService;
import com.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private UserService userService;

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
}