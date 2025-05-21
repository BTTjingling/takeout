package com.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.entity.Address;
import java.util.List;

public interface AddressService extends IService<Address> {

    List<Address> getUserAddresses(Long userId);

    void setDefaultAddress(Long userId, Long addressId);

    // 新增用户地址
    void addUserAddress(Long userId, Address address);

    // 修改用户地址
    void updateUserAddress(Long addressId, Address address);

    // 删除用户地址
    void deleteUserAddress(Long addressId);
}
