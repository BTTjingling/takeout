package com.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.entity.Address;
import java.util.List;

public interface AddressService extends IService<Address> {

    List<Address> getUserAddresses(Long userId);

    void setDefaultAddress(Long userId, Long addressId);
}
