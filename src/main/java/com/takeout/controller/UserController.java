package com.takeout.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.dto.RegisterRequest;
import com.takeout.entity.User;
import com.takeout.pojo.Result;
import com.takeout.service.OrderService;
import com.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.takeout.service.MerchantService;
import com.takeout.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class); // 定义 logger 变量
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @GetMapping("/info")
    public User getUserInfo(@RequestParam Long userId) {
        return userService.getUserInfo(userId);
    }
    @PostMapping("/submitOrder")
    public String submitOrder(@RequestBody Order order) {
        return userService.submitOrder(order);
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            // 根据用户类型进行注册
            if (request.getUserType() == 1) {
                userService.registerUser(request);
            } else if (request.getUserType() == 2) {
                merchantService.registerMerchant(request);
            } else {
                return ResponseEntity.badRequest().body("无效的用户类型");
            }
            return ResponseEntity.ok("注册成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("注册失败: " + e.getMessage());
        }
    }
    /**
     * 用户取消订单
     * @param orderId 订单 ID
     * @return 操作结果
     */
    @PostMapping("/orders/{orderId}/cancel")
    public Result cancelOrder(@PathVariable Long orderId) {
        boolean success = orderService.cancelOrder(orderId);
        if (success) {
            return Result.success("订单取消成功");
        } else {
            return Result.error(500, "订单取消失败");
        }
    }
    /**
     * 用户查看自己的订单信息
     * @param userId 用户 ID
     * @param pageNum 页码，默认值为 1
     * @param pageSize 每页数量，默认值为 10
     * @return 分页后的订单列表
     */
    @GetMapping("/orders/list")
    public Page<Order> getMyOrders(@RequestParam Long userId,
                                   @RequestParam(defaultValue = "1") Long pageNum,
                                   @RequestParam(defaultValue = "10") Long pageSize) {
        logger.info("调用 getMyOrders 接口，userId: {}, pageNum: {}, pageSize: {}", userId, pageNum, pageSize);
        Page<Order> page = new Page<>(pageNum, pageSize);
        return orderService.getOrdersByUserId(userId, page);
    }

}