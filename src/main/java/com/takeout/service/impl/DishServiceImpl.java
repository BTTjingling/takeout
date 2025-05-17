package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.Dish;
import com.takeout.entity.Merchant;
import com.takeout.mapper.DishMapper;
import com.takeout.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.takeout.entity.Merchant;
import com.takeout.service.MerchantService;
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private MerchantService merchantService;
    @Override
    public Page<Dish> getDishesByShopId(Long shopId, Page<Dish> page) {
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", shopId); // 严格按shop_id过滤
        return baseMapper.selectPage(page, queryWrapper);
    }
    @Override
    public Page<Merchant> searchMerchantsByDishName(String dishName, Long pageNum, Long pageSize) {
        // 构建关联查询条件
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        return merchantService.lambdaQuery()
                .exists("SELECT 1 FROM dish WHERE shop_id = shop_id AND name LIKE {0}", "%" + dishName + "%")
                .page(page);
    }
    @Override
    public Page<Dish> listAvailableDishesByShop(Long shopId, Long pageNum, Long pageSize) {
        Page<Dish> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", shopId)
                .eq("is_available", 1); // 同时满足店铺ID和上架状态
        return this.page(page, queryWrapper);
    }


}