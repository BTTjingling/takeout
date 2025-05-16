package com.takeout.controller;
import com.takeout.dto.RegisterRequest;
import com.takeout.entity.User;
import com.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.takeout.service.MerchantService;
import com.takeout.entity.Order;
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private MerchantService merchantService;
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
}