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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    @TableField("Ostatus")
    private String Ostatus;
    public static final List<String> ALLOWED_STATUSES = Arrays.asList(
            "待接单", "制作中", "配送中", "已完成", "用户已取消","商家已取消"
    );
    @TableField(exist = false)
    private String merchantName;

    @TableField(exist = false)
    private String dishName;
    public boolean isValidStatus() {
        return ALLOWED_STATUSES.contains(Ostatus);
    }


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