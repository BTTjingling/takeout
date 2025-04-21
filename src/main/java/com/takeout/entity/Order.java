package com.takeout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order")
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
}