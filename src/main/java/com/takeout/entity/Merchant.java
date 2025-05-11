package com.takeout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("shop")
public class Merchant {
    // 主键：建议使用 shop_id，与数据库一致
    @TableId(value = "shop_id", type = IdType.AUTO)
    private Long shopId;

    // 登录相关字段
    private String phone;
    private String password;

    // 商家基本信息
    private String name;
    private String description;
    private Integer status; // 0关闭，1营业
    private String address;
    // 评分（示例默认满分 5.0）
    private Double rating;
    private String avatar; // 头像路径
}
