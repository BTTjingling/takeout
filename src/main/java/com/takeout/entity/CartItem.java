package com.takeout.entity;

import lombok.Data;

@Data
public class CartItem {
    private Long userId;
    private Long productId;
    private int quantity;

    // 添加构造函数
    public CartItem(Long userId, Long productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    // 无参构造函数
    public CartItem() {
    }
}
