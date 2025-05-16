package com.takeout.service;
import com.takeout.dto.RegisterRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.entity.User;
import com.takeout.entity.Order;
public interface UserService extends IService<User> {
    User getUserInfo(Long userId);
    String submitOrder(Order order);
    void registerUser(RegisterRequest request);
}