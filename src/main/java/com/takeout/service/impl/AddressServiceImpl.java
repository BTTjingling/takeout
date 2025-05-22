package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.Address;
import com.takeout.exception.BusinessException;
import com.takeout.mapper.AddressMapper;
import com.takeout.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Service
public class AddressServiceImpl
     extends ServiceImpl<AddressMapper, Address>
     implements AddressService {

    @Autowired
    private AddressMapper addressMapper;
          
    @Override
    public List<Address> getUserAddresses(Long userId) {
        QueryWrapper<Address> query = new QueryWrapper<>();
        query.eq("user_id", userId)
             .orderByDesc("is_default");
        return baseMapper.selectList(query);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        // 取消其他默认地址
        baseMapper.cancelOtherDefault(userId, addressId);

        // 设置当前地址为默认
        Address address = new Address();
        address.setAddressId(addressId);
        address.setIsDefault(true);

        int result = baseMapper.updateById(address);
        if (result != 1) {
            throw new BusinessException("设置默认地址失败");
        }
    }

    // 新增用户地址
    @Override
    public void addUserAddress(Long userId, Address address) {
        try {
            address.setUserId(userId);
            // 如果是第一个地址，可以设置为默认
            if (addressMapper.selectList(null).isEmpty()) {
                address.setIsDefault(true);
            }
            addressMapper.insert(address);  // 使用 BaseMapper 提供的插入方法
        } catch (Exception e) {
            throw new BusinessException("新增地址失败: " + e.getMessage());
        }
    }

    // 修改用户地址
    @Override
    public void updateUserAddress(Long addressId, Address address) {
        try {
            address.setAddressId(addressId);
            addressMapper.updateById(address);  // 使用 BaseMapper 提供的更新方法
        } catch (Exception e) {
            throw new BusinessException("修改地址失败: " + e.getMessage());
        }
    }

    // 删除用户地址
    @Override
    public void deleteUserAddress(Long addressId) {
        try {
            addressMapper.deleteById(addressId);  // 使用 BaseMapper 提供的删除方法
        } catch (Exception e) {
            throw new BusinessException("删除地址失败: " + e.getMessage());
        }
    }
}
