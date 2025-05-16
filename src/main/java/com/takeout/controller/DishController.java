package com.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.Dish;
import com.takeout.entity.Merchant;
import com.takeout.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    // 添加菜品
    @PostMapping
    public String createDish(@RequestBody Dish dish) {
        try {
            boolean flag = dishService.save(dish);
            return flag ? "菜品添加成功" : "菜品添加失败";
        } catch (Exception e) {
            e.printStackTrace();
            return "菜品添加失败: " + e.getMessage();
        }
    }

    // 删除菜品（根据 dishId 删除）
    @DeleteMapping("/{dishId}")
    public String deleteDish(@PathVariable Long dishId) {
        boolean flag = dishService.removeById(dishId);
        return flag ? "菜品删除成功" : "菜品删除失败";
    }

    // 修改菜品信息
    @PutMapping("/{dishId}")
    public String updateDish(@PathVariable Long dishId, @RequestBody Dish dish) {
        try {
            dish.setDishId(dishId); // 确保dishId被正确设置
            boolean flag = dishService.updateById(dish);
            return flag ? "菜品更新成功" : "菜品更新失败";
        } catch (Exception e) {
            e.printStackTrace();
            return "菜品更新失败: " + e.getMessage();
        }
    }

    // 查询菜品详情
    @GetMapping("/{dishId}")
    public Dish getDish(@PathVariable Long dishId) {
        return dishService.getById(dishId);
    }
    // 新增：根据菜品名称模糊搜索商户（分页）
    // 修改后的搜索商户接口
    @GetMapping("/search/merchants")
    public Page<Merchant> searchMerchantsByDishName(
            @RequestParam String dishName,
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize) {
        return dishService.searchMerchantsByDishName(dishName, pageNum, pageSize);
    }

    // 根据商家ID分页查询菜品 & 支持按名称进行模糊搜索
    @GetMapping("/shop/{shopId}")
    public Page<Dish> listDishesByShop(@PathVariable Long shopId,
                                       @RequestParam(defaultValue = "1") Long pageNum,
                                       @RequestParam(defaultValue = "10") Long pageSize,
                                       @RequestParam(required = false) String name) {
        Page<Dish> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id", shopId);
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like("name", name).or().like("description", name);
        }
        return dishService.page(page, queryWrapper);
    }
}
