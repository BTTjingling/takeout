package com.takeout.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeout.entity.Dish;
import com.takeout.entity.Order;
import com.takeout.entity.Merchant;
import com.takeout.service.DishService;
import com.takeout.service.OrderService;
import com.takeout.service.MerchantService;
import com.takeout.service.MerchantChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MerchantChatServiceImpl implements MerchantChatService {

    private static final Logger logger = LoggerFactory.getLogger(MerchantChatServiceImpl.class);

    @Value("${deepseek.api.url}")
    private String deepSeekApiUrl;

    @Value("${deepseek.api.key}")
    private String deepSeekApiKey;

    private final OrderService orderService;
    private final DishService dishService;
    private final MerchantService merchantService;
    private final ObjectMapper objectMapper;

    public MerchantChatServiceImpl(OrderService orderService, DishService dishService,
                                   MerchantService merchantService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.dishService = dishService;
        this.merchantService = merchantService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getChatResponse(Long merchantId, String question) {
        try {
            // 获取商家信息
            Merchant merchant = merchantService.getMerchantById(merchantId);

            // 获取商家的菜品信息
            List<Dish> dishes = dishService.getDishesByShopId(merchantId, null).getRecords();

            Page<Order> orderPage = new Page<>(1, Integer.MAX_VALUE); // 获取第一页，最大数量的记录
            List<Order> orders = orderService.getOrdersByShopId(merchantId, orderPage, null, null, null).getRecords();

            // 构建请求提示语
            String prompt = String.format("你是一位专业的餐饮运营顾问。请基于以下商家 %s（ID：%d）的现有相关数据给出回答，无需提及需要更多数据。菜品信息：%s，所有订单信息：%s。问题：%s",
                    merchant.getName(), merchantId, serializeDishes(dishes), serializeOrders(orders), question);

            // 构建符合 DeepSeek API 要求的请求体
            var requestBody = new java.util.HashMap<String, Object>();
            requestBody.put("model", "deepseek-chat");
            var messages = new java.util.ArrayList<java.util.Map<String, String>>();
            var userMessage = new java.util.HashMap<String, String>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + deepSeekApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<java.util.Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 打印请求信息
            logger.info("请求的 DeepSeek API 地址: {}", deepSeekApiUrl);
            logger.info("请求头: {}", headers);
            logger.info("请求体: {}", requestBody);

            // 调用 DeepSeek API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(deepSeekApiUrl, HttpMethod.POST, entity, String.class);

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK) {
                java.util.Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), java.util.Map.class);
                java.util.List<java.util.Map<String, Object>> choices = (java.util.List<java.util.Map<String, Object>>) responseMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    java.util.Map<String, Object> message = (java.util.Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            logger.error("获取商家 AI 聊天回复失败", e);
        }
        return "抱歉，暂时无法回答你的问题，请稍后再试。";
    }

    private String serializeOrders(List<Order> orders) {
        try {
            return objectMapper.writeValueAsString(orders);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private String serializeDishes(List<Dish> dishes) {
        try {
            return objectMapper.writeValueAsString(dishes);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}