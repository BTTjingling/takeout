package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.Address;
import com.takeout.exception.BusinessException;
import com.takeout.mapper.AddressMapper;
import com.takeout.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl
     extends ServiceImpl<AddressMapper, Address>
     implements AddressService {

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
}
