package com.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.User;
import com.takeout.mapper.UserMapper;
import com.takeout.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getUserInfo(Long userId) {
        // 使用MyBatis-Plus提供的方法查询用户信息
        return this.getById(userId);
    }
}