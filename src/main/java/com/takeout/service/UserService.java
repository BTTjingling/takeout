package com.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.entity.User;

public interface UserService extends IService<User> {
    User getUserInfo(Long userId);
}