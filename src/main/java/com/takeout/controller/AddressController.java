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

    // 新增地址
    @PostMapping("/user/{userId}")
    public void addUserAddress(@PathVariable Long userId, @RequestBody Address address) {
        try {
            addressService.addUserAddress(userId, address);
        } catch (Exception e) {
            throw new BusinessException("新增地址失败: " + e.getMessage());
        }
    }

    // 修改地址
    @PutMapping("{addressId}")
    public void updateUserAddress(@PathVariable Long addressId, @RequestBody Address address) {
        try {
            addressService.updateUserAddress(addressId, address);
        } catch (Exception e) {
            throw new BusinessException("修改地址失败: " + e.getMessage());
        }
    }

    // 删除地址
    @DeleteMapping("{addressId}")
    public void deleteUserAddress(@PathVariable Long addressId) {
        try {
            addressService.deleteUserAddress(addressId);
        } catch (Exception e) {
            throw new BusinessException("删除地址失败: " + e.getMessage());
        }
    }
}
