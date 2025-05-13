package com.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.User;
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
    @Override
    public User getUserInfo(Long userId) {
        // 使用MyBatis-Plus提供的方法查询用户信息
        return this.getById(userId);
    }
    @Override
    public String submitOrder(Order order) {
        try {
            // 保存订单到数据库
            orderMapper.insert(order);
            return "订单提交成功";
        } catch (Exception e) {
            // 处理异常情况
            e.printStackTrace();
            return "订单提交失败";
        }
    }
}