package com.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.entity.CartItem;
import com.takeout.entity.Order;

import java.util.List;

public interface CartService extends IService<CartItem> {
    CartItem addToCart(Long userId, Long productId, int quantity);
    Order checkout(Long userId, double totalAmount);
    List<CartItem> getCartItems(Long userId);
}