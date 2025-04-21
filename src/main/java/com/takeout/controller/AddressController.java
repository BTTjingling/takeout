package com.takeout.controller;

import com.takeout.entity.Address;
import com.takeout.exception.BusinessException;
import com.takeout.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // 获取用户的所有地址
    @GetMapping("/user/{userId}")
    public List<Address> getUserAddresses(@PathVariable Long userId) {
        try {
            return addressService.getUserAddresses(userId);
        } catch (Exception e) {
            throw new BusinessException("获取地址失败: " + e.getMessage());
        }
    }

    // 设置默认地址
    @PutMapping("/set-default")
    public void setDefaultAddress(@RequestParam Long userId, @RequestParam Long addressId) {
        try {
            addressService.setDefaultAddress(userId, addressId);
        } catch (BusinessException e) {
            throw new BusinessException("设置默认地址失败: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("系统错误: " + e.getMessage());
        }
    }
}
