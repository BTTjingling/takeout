package com.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.entity.Merchant;
import org.apache.ibatis.javassist.NotFoundException;
import com.takeout.dto.RegisterRequest;
public interface MerchantService extends IService<Merchant> {
    Merchant getMerchantById(Long shopId) throws NotFoundException;
    // 新增分页查询方法
    Page<Merchant> pageMerchants(Page<Merchant> page, String name);
    void registerMerchant(RegisterRequest request);
    void updateMerchantStatus(Integer shopId, Integer status);
}