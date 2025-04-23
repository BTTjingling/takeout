package com.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.entity.CartItem;
import com.takeout.entity.Order;
import com.takeout.mapper.CartItemMapper;
import com.takeout.mapper.OrderMapper;
import com.takeout.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartService {
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public CartItem addToCart(Long userId, Long productId, int quantity) {
        CartItem cartItem = new CartItem(userId, productId, quantity);
        this.save(cartItem);
        return cartItem;
    }

    @Override
    public Order checkout(Long userId, double totalAmount) {
        Order order = new Order();
        order.setUserId(userId);
        // 将 Date 转换为 LocalDateTime
        LocalDateTime now = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        order.setOrderTime(now);
        // 将 double 转换为 BigDecimal
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));
        orderMapper.insert(order);
        return order;
    }

    @Override
    public List<CartItem> getCartItems(Long userId) {
        return this.lambdaQuery().eq(CartItem::getUserId, userId).list();
    }
}