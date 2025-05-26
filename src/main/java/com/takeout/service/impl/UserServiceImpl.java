package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.dto.RegisterRequest;
import com.takeout.entity.Dish;
import com.takeout.entity.Merchant;
import com.takeout.entity.User;
import com.takeout.mapper.DishMapper;
import com.takeout.mapper.MerchantMapper;
import com.takeout.mapper.UserMapper;
import com.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.takeout.entity.Order;
import com.takeout.mapper.OrderMapper;
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserInfo(Long userId) {
        // 使用MyBatis-Plus提供的方法查询用户信息
        return this.getById(userId);
    }
    @Override
    public String submitOrder(Order order) {
        try {
            // 根据商家 ID 获取商家信息
            Merchant merchant = merchantMapper.selectById(order.getShopId());
            if (merchant != null) {
                // 设置商家名称到订单实体
                order.setMerchantName(merchant.getName());
            }
            Dish dish = dishMapper.selectById(order.getDishId());
            if (dish != null) {
                // 设置菜品名称到订单实体
                order.setDishName(dish.getName());
            }
            // 插入订单记录
            orderMapper.insertOrder(order);
            return "订单提交成功";
        } catch (Exception e) {
            // 处理异常情况
            e.printStackTrace();
            return "订单提交失败";
        }
    }
    @Override
    public void registerUser(RegisterRequest request) {
        // 检查用户名是否已存在
        if (lambdaQuery().eq(User::getUsername, request.getUsername()).count() > 0) {
            throw new RuntimeException("用户名 " + request.getUsername() + " 已被注册，请尝试其他用户名");
        }

        // 检查手机号是否已注册
        if (lambdaQuery().eq(User::getPhone, request.getPhone()).count() > 0) {
            throw new RuntimeException("手机号 " + request.getPhone() + " 已被注册，请使用其他手机号");
        }

        // 创建用户实体
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        // 保存用户
        save(user);
    }
    @Override
    public Page<User> getAllUsers(Page<User> page) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // 查找用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户未找到");
        }

        // 验证原密码是否正确
        if (!oldPassword.equals(user.getPassword())) {  // 明文对比
            return false; // 原密码错误
        }

        // 更新新密码
        user.setPassword(newPassword);  // 直接更新明文密码
        int rowsAffected = userMapper.updateById(user);  // 更新数据库

        return rowsAffected > 0;  // 如果更新成功，返回true
    }
}
