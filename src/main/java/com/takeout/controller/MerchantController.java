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
    @PostMapping("/uploadAvatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam Long shopId,
                                          @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }

        // 创建上传目录
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadPath + File.separator + fileName);

        try {
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

    @GetMapping("/avatar")
    public ResponseEntity<?> getAvatar(@RequestParam Long shopId) {
        Merchant merchant = merchantService.getById(shopId);
        if (merchant != null && merchant.getAvatar() != null) {
            return ResponseEntity.ok(merchant.getAvatar());
        }
        return ResponseEntity.status(404).body("商家头像不存在");
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

}
