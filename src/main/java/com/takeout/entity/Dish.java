package com.takeout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dish")
public class Dish {
    // 主键
    @TableId(value = "dish_id", type = IdType.AUTO)
    private Long dishId;

    // 外键：所属商家
    private Long shopId;

    private String name;
    private String description;
    private Double price;
    private String category;

    // 是否上架：1-是，0-否
    private Integer isAvailable;
    // 菜品库存
    private Integer inventory;
}
