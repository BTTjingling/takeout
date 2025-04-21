package com.takeout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.takeout.mapper")
public class TakeoutApplication {
    public static void main(String[] args) {
        SpringApplication.run(TakeoutApplication.class, args);
    }
} 