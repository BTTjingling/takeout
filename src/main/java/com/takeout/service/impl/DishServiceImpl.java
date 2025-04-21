package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.Dish;
import com.takeout.mapper.DishMapper;
import com.takeout.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Override
    public Page<Dish> getDishesByShopId(Long shopId, Page<Dish> page) {
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", shopId); // 严格按shop_id过滤
        return baseMapper.selectPage(page, queryWrapper);
    }
}