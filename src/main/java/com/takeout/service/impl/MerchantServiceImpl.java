package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.dto.ChangePasswordRequest;
import com.takeout.dto.RegisterRequest;
import com.takeout.entity.Dish;
import com.takeout.entity.Merchant;
import com.takeout.mapper.DishMapper;
import com.takeout.mapper.MerchantMapper;
import com.takeout.service.DishService;
import com.takeout.service.MerchantService;
import com.takeout.service.OrderService;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.takeout.service.DishService;
import java.math.BigDecimal;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {
    private static final Logger logger = LoggerFactory.getLogger(MerchantServiceImpl.class);

    @Resource
    private MerchantMapper merchantMapper;
    @Autowired
    private DishMapper dishMapper;  //
    @Override
    public Merchant getMerchantById(Long shopId) throws NotFoundException {
        try {
            Merchant merchant = getById(shopId);
            if (merchant == null) {
                throw new NotFoundException("商家不存在，shopId: " + shopId);
            }
            return merchant;
        } catch (Exception e) {
            logger.error("查询商家信息失败，shopId: {}", shopId, e); // ✅ 添加日志记录
            throw e; // ✅ 抛出异常，由控制器捕获
        }
    }
    @Override
    public Page<Merchant> pageMerchants(Page<Merchant> page, String name) {
        QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like("name", name)
                    .or()
                    .like("description", name);
        }
        return baseMapper.selectPage(page, queryWrapper);
    }
    @Override
    public void registerMerchant(RegisterRequest request) {
        // 检查用户名是否已存在
        if (lambdaQuery().eq(Merchant::getName, request.getUsername()).count() > 0) {
            logger.error("用户名已存在: {}", request.getUsername());
            throw new RuntimeException("用户名 " + request.getUsername() + " 已被注册，请尝试其他用户名");
        }

        // 检查手机号是否已注册
        QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        if (count(queryWrapper) > 0) {
            logger.error("手机号已注册: {}", request.getPhone());
            throw new RuntimeException("手机号 " + request.getPhone() + " 已被注册，请使用其他手机号");
        }

        // 创建商家实体
        Merchant merchant = new Merchant();
        merchant.setName(request.getUsername()); // 使用name字段存储用户名
        merchant.setPassword(request.getPassword());
        merchant.setPhone(request.getPhone());
        merchant.setStatus(0); // 默认状态为关闭
        merchant.setRating(5.0); // 默认评分5.0
        merchant.setAddress(""); // 默认空地址
        merchant.setDescription(""); // 默认空描述
        merchant.setAvatar(""); // 默认空头像路径
        merchant.setDevfee(BigDecimal.valueOf(0.0));
        merchant.setMinprice(BigDecimal.valueOf(0.0));

        // 保存商家
        save(merchant);
    }

    @Override
    @Transactional
    public void updateMerchantStatus(Integer shopId, Integer status) {
        Merchant merchant = baseMapper.selectById(shopId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        merchant.setStatus(status);
        baseMapper.updateById(merchant);
    }
    @Override
    public Page<Merchant> getAllMerchants(Page<Merchant> page) {
        QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
        return baseMapper.selectPage(page, queryWrapper);
    }
    @Override
    public boolean changePassword(ChangePasswordRequest request) {
        Merchant merchant = merchantMapper.selectById(request.getShopId());
        if (merchant == null) {
            logger.warn("商家不存在，shopId: {}", request.getShopId());
            return false;
        }
        if (!merchant.getPassword().equals(request.getOldPassword())) {
            logger.warn("原密码错误，shopId: {}", request.getShopId());
            return false;
        }
        merchant.setPassword(request.getNewPassword());
        int rows = merchantMapper.updateById(merchant);
        return rows > 0;
    }
    @Override
    public Integer getTotalDishes(Long shopId) {
        // 使用DishMapper来查询Dish实体
        return dishMapper.countByShopId(shopId);
    }

}