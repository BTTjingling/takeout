package com.takeout.controller;

import com.takeout.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @Autowired
    private UserMapper userMapper;
    
    @GetMapping("/test")
    public String test() {
        return "Hello, Takeout System!";
    }
    
    @GetMapping("/db-test")
    public String dbTest() {
        long count = userMapper.selectCount(null);
        return "Database connection successful! User count: " + count;
    }
} 