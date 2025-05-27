package com.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.Dish;
import com.takeout.entity.Merchant;
import com.takeout.pojo.Result;
import com.takeout.service.DishService;
import com.takeout.service.MerchantService;
import com.takeout.service.OrderService;
import com.takeout.service.impl.MerchantServiceImpl;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.*;
import java.io.File;
import java.io.IOException;

import com.takeout.dto.ChangePasswordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import com.takeout.dto.UpdateStatusRequest;
@RestController
@RequestMapping("/api/merchants")
public class MerchantController {
    private static final Logger logger = LoggerFactory.getLogger(MerchantServiceImpl.class);
    private static final List<String> ALLOWED_STATUSES = Arrays.asList("未接单", "已接单制作中", "配送中", "已完成", "商家已取消","用户已取消");
    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private DishService dishService;
    @Value("${spring.web.upload-path}")
    private String uploadPath;

    // 创建商家
    @PostMapping
    public String createMerchant(@RequestBody Merchant merchant) {
        boolean flag = merchantService.save(merchant);
        return flag ? "创建成功" : "创建失败";
    }

    // 删除商家（根据 shopId 删除）
    @DeleteMapping("/{shopId}")
    public String deleteMerchant(@PathVariable Long shopId) {
        boolean flag = merchantService.removeById(shopId);
        return flag ? "删除成功" : "删除失败";
    }

    // 修改商家信息
    @PutMapping("/{shopId}")
    public String updateMerchant(@PathVariable Long shopId, @RequestBody Merchant merchant) {
        merchant.setShopId(shopId);
        boolean flag = merchantService.updateById(merchant);
        return flag ? "更新成功" : "更新失败";
    }

    // 获取商家信息
    @GetMapping("/{shopId}")
    public Result<Merchant> getMerchant(@PathVariable Long shopId) {
        try {
            Merchant merchant = merchantService.getMerchantById(shopId);
            return Result.success(merchant);
        } catch (NotFoundException e) {
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            logger.error("获取商家信息失败，shopId: {}", shopId, e); // ✅ 添加日志记录
            return Result.error(500, "服务器内部错误: " + e.getMessage());
        }
    }

    // 获取菜品列表
    @GetMapping("/{shopId}/dishes")
    public Result<Page<Dish>> getDishes(
            @PathVariable Long shopId,
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize
    ) {
        Page<Dish> page = new Page<>(pageNum, pageSize);
        Page<Dish> dishPage = dishService.getDishesByShopId(shopId, page); // 调用新方法
        if (dishPage.getRecords().isEmpty()) {
            return new Result<>(404, "菜品不存在", null);
        }
        return new Result<>(200, "获取成功", dishPage);
    }
    // 分页查询 & 简单搜索（例如：通过店铺名称模糊查询）
    @GetMapping("/list")
    public Page<Merchant> listMerchants(@RequestParam(defaultValue = "1") Long pageNum,
                                        @RequestParam(defaultValue = "10") Long pageSize,
                                        @RequestParam(required = false) String name) {
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
        // 只查询上架的商家
        queryWrapper.eq("status", 1);
        if (name != null && !name.trim().isEmpty()) {
            // 可以利用数据库支持的全文索引或 LIKE 实现模糊查询
            queryWrapper.like("name", name).or().like("description", name);
        }
        return merchantService.page(page, queryWrapper);
    }
    @GetMapping("/dishes/total")
    public Integer getTotalDishes(@RequestParam Long shopId) {
        return merchantService.getTotalDishes(shopId);
    }


    @GetMapping("/revenue/today")
    public Double getTodayRevenue(@RequestParam Long shopId) {
        return orderService.getTodayRevenue(shopId);
    }
    @GetMapping("/orders/pending-count")
    public Result<Long> getPendingOrdersCount(@RequestParam Long shopId) {
        try {
            long count = orderService.countPendingOrders(shopId);
            return Result.success(count);
        } catch (Exception e) {
            logger.error("获取待处理订单数量失败，shopId: {}", shopId, e);
            return Result.error(500, "获取待处理订单数量失败");
        }
    }
    @GetMapping("/revenue/last7days")
    public Result<Map<String, Double>> getRevenueLast7Days(@RequestParam Long shopId) {
        try {
            Map<String, Double> revenueData = orderService.getRevenueLast7Days(shopId);
            return Result.success(revenueData);
        } catch (Exception e) {
            logger.error("获取7天营业额数据失败，shopId: {}", shopId, e);
            return Result.error(500, "获取7天营业额数据失败");
        }
    }

    @PostMapping("/uploadAvatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam Long shopId,
                                          @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }

        // 修改为前端项目的public/images目录
        String uploadDir = "E:/github project/takeout/takeout-frontend/public/images/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成固定格式文件名: Avatar+shopId.png
        String fileName = "Avatar" + shopId + ".png";
        File dest = new File(uploadDir + fileName);

        try {
            // 先删除旧的头像文件（如果存在）
            if (dest.exists()) {
                dest.delete();
            }

            file.transferTo(dest);

            // 更新商家头像路径
            Merchant merchant = merchantService.getById(shopId);
            if (merchant != null) {
                merchant.setAvatar(fileName);
                merchantService.updateById(merchant);
                return ResponseEntity.ok("头像上传并更新成功");
            }
            return ResponseEntity.status(404).body("商家不存在");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("上传失败");
        }
    }



    @GetMapping("/avatar/{shopId}")
    public ResponseEntity<?> getAvatar(@PathVariable Long shopId) {
        Merchant merchant = merchantService.getById(shopId);
        if (merchant != null && merchant.getAvatar() != null) {
            // 修改为前端可直接访问的路径
            String avatarUrl = "/images/" + merchant.getAvatar();
            return ResponseEntity.ok(avatarUrl);
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestBody UpdateStatusRequest request) {
        try {
            merchantService.updateMerchantStatus(request.getShopId(), request.getStatus());
            return Result.success("状态更新成功");
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
    /**
     * 修改商家密码
     * @param request 修改密码请求对象
     * @return 操作结果
     */
    @PostMapping("/password")
    public Result changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            boolean success = merchantService.changePassword(request);
            if (success) {
                return Result.success("密码修改成功");
            } else {
                return Result.error(400, "密码修改失败，可能是商家不存在或原密码错误");
            }
        } catch (Exception e) {
            logger.error("修改商家密码失败，shopId: {}", request.getShopId(), e);
            return Result.error(500, "服务器内部错误: " + e.getMessage());
        }
    }
    /**
     * 商家更新订单状态
     * @param orderId 订单 ID
     * @param newStatus 新的订单状态
     * @return 操作结果
     */
    @PostMapping("/orders/{orderId}/status")
    public Result updateOrderStatus(@PathVariable Long orderId, @RequestParam String newStatus) {
        if (!ALLOWED_STATUSES.contains(newStatus)) {
            return Result.error(400, "不合法的订单状态值");
        }
        boolean success = orderService.updateOrderStatus(orderId, newStatus);
        if (success) {
            return Result.success("订单状态更新成功");
        } else {
            return Result.error(500, "订单状态更新失败");
        }
    }


    // 上传菜品图片，并返回图片URL
    @PostMapping("/uploadImage")
    public String uploadDishImage(@RequestParam("file") MultipartFile file) {
        // 设定保存文件的目录路径
        String uploadDir = "E:/github project/takeout/takeout-frontend/public/images/";  // 你指定的保存目录
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

            // 返回图片的相对路径
            String imageUrl = "/images/" + newFileName;  // 假设返回图片URL（相对路径）

            // 返回图片URL
            return imageUrl;  // 只返回图片的URL
        } catch (IOException e) {
            e.printStackTrace();
            return "图片上传失败： " + e.getMessage();
        }
    }

}
