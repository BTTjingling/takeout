package com.takeout.controller;

import com.takeout.entity.CartItem;
import com.takeout.entity.Order;
import com.takeout.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public CartItem addToCart(@RequestParam Long userId, @RequestParam Long productId, @RequestParam int quantity) {
        return cartService.addToCart(userId, productId, quantity);
    }

    @PostMapping("/checkout")
    public Order checkout(@RequestParam Long userId, @RequestParam double totalAmount) {
        return cartService.checkout(userId, totalAmount);
    }

    @GetMapping("/items")
    public List<CartItem> getCartItems(@RequestParam Long userId) {
        return cartService.getCartItems(userId);
    }
}