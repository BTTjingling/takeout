package com.takeout.controller;

import com.takeout.entity.User;
import com.takeout.entity.Merchant;
import com.takeout.entity.Admin;
import com.takeout.mapper.UserMapper;
import com.takeout.mapper.MerchantMapper;
import com.takeout.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import com.takeout.pojo.Result;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private AdminMapper adminMapper;

    @PostMapping("/user")
    public ResponseEntity<?> userLogin(@RequestBody LoginRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        User user = userMapper.selectOne(queryWrapper);

        if (user != null && user.getPassword().equals(request.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("message", "User login successful!");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("Invalid credentials!");
    }


    @PostMapping("/merchant")
    public ResponseEntity<?> merchantLogin(@RequestBody LoginRequest request) {
        try {
            logger.info("商家登录请求数据：phone={}, password={}", request.getPhone(), request.getPassword());

            // 检查请求参数是否完整
            if (request.getPhone() == null || request.getPassword() == null) {
                logger.error("登录参数不完整：phone={}, password={}", request.getPhone(), request.getPassword());
                return ResponseEntity.badRequest().body("登录参数不完整");
            }

            // 查询商家信息
            QueryWrapper<Merchant> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", request.getPhone());
            Merchant merchant = merchantMapper.selectOne(queryWrapper);

            // 检查商家是否存在
            if (merchant == null) {
                logger.warn("商家不存在：phone={}", request.getPhone());
                return ResponseEntity.ok("Invalid credentials!"); // 返回 401 错误信息
            }

            // 检查密码是否匹配
            if (!merchant.getPassword().equals(request.getPassword())) {
                logger.warn("密码错误：phone={}", request.getPhone());
                return ResponseEntity.ok("Invalid credentials!"); // 返回 401 错误信息
            }

            // 登录成功
            logger.info("商家登录成功：phone={}", request.getPhone());
            return ResponseEntity.ok(new Result<>(
                    200,
                    "Login success",
                    Map.of(
                            "shopId", merchant.getShopId(),
                            "role", "merchant"
                    )
            ));

        } catch (DataAccessException e) {
            // 处理数据库异常
            logger.error("数据库访问异常", e);
            return ResponseEntity.internalServerError().body("数据库访问异常：" + e.getMessage());
        } catch (Exception e) {
            // 处理其他异常
            logger.error("商家登录异常", e);
            return ResponseEntity.internalServerError().body("Login failed: " + e.getMessage());
        }
    }
    @PostMapping("/admin")
    public String adminLogin(@RequestBody LoginRequest request) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        Admin admin = adminMapper.selectOne(queryWrapper);
        if (admin != null && admin.getPassword().equals(request.getPassword())) {
            return "Admin login successful! Redirecting to admin dashboard...";
        }
        return "Invalid credentials!";
    }
}

@Data
class LoginRequest {
    private String username;
    private String password;
    private String role;
    private String phone;
} 