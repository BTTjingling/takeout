package com.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.Dish;
import com.takeout.entity.Merchant;
import com.takeout.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    
    // 上传菜品图片，并返回图片URL
    @PostMapping("/{dishId}/uploadImage")
    public String uploadDishImage(@PathVariable Long dishId, @RequestParam("file") MultipartFile file) {
        // 设定保存文件的目录路径
        String uploadDir = "E:/idea.project/takeout/takeout-frontend/public/images/";  // 你指定的保存目录
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();  // 如果目录不存在则创建
        }

        // 获取上传的文件名
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            return "文件名为空";
        }

        // 获取当前时间戳，避免文件名重复
        String timestamp = String.valueOf(System.currentTimeMillis());
        // 在文件名前加上时间戳，以确保文件名唯一
        String newFileName = timestamp + "_" + fileName;

        // 设置文件的完整路径
        File dest = new File(uploadDir + newFileName);
        try {
            // 保存文件到本地
            file.transferTo(dest);

            // 假设返回图片的本地路径或相对路径
            String imageUrl = "/images/" + newFileName;  // 假设返回图片URL（相对路径）
            System.out.println("Generated image URL: " + imageUrl);
            // 更新菜品的图片URL
            Dish dish = dishService.getById(dishId);
            if (dish != null) {
                dish.setImageUrl(imageUrl);  // 设置图片URL
                boolean flag = dishService.updateById(dish);  // 更新菜品信息
                return flag ? "图片上传成功" : "图片上传失败";
            } else {
                return "菜品不存在";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "图片上传失败： " + e.getMessage();
        }
    }

    // 获取菜品的图片URL
    @GetMapping("/{dishId}/image")
    public String getDishImage(@PathVariable Long dishId) {
        // 添加日志
        System.out.println("Fetching image for dishId: " + dishId);

        Dish dish = dishService.getById(dishId);
        if (dish != null) {
            System.out.println("Dish found: " + dish.getName());
            return dish.getImageUrl();  // 返回菜品的图片URL
        } else {
            System.out.println("Dish with id " + dishId + " not found.");
            return "菜品不存在";
        }
    }
}
