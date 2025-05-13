package com.takeout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@TableName("`order`")
public class Order {
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("shop_id")
    private Long shopId;

    @TableField("dish_id")
    private Long dishId;

    @TableField("quantity")
    private Integer quantity;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("order_time")
    private LocalDateTime orderTime;

    // 无参构造函数
    public Order() {
    }

    // 添加 (java.lang.Long, java.util.Date, double) 构造函数
    public Order(Long userId, Date orderDate, double totalAmount) {
        this.userId = userId;
        this.orderTime = LocalDateTime.ofInstant(orderDate.toInstant(), ZoneId.systemDefault());
        this.totalAmount = BigDecimal.valueOf(totalAmount);
    }
}