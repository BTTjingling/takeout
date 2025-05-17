package com.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.entity.Dish;
import com.takeout.entity.Merchant;

public interface DishService extends IService<Dish> {
    // 新增方法：根据商家ID分页查询菜品
    Page<Dish> getDishesByShopId(Long shopId, Page<Dish> page);
    Page<Dish> listAvailableDishesByShop(Long shopId, Long pageNum, Long pageSize);

    // 新增菜品关联商户查询方法
    Page<Merchant> searchMerchantsByDishName(String dishName, Long pageNum, Long pageSize);

}
