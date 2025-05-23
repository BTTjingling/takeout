package com.takeout.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeout.entity.Dish;
import com.takeout.entity.Order;
import com.takeout.service.DishService;
import com.takeout.service.OrderService;
import com.takeout.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    @Value("${deepseek.api.url}")
    private String deepSeekApiUrl;

    @Value("${deepseek.api.key}")
    private String deepSeekApiKey;

    private final OrderService orderService;
    private final DishService dishService;
    private final ObjectMapper objectMapper;

    public RecommendationServiceImpl(OrderService orderService, DishService dishService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.dishService = dishService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Map<String, Object>> getRecommendations(Long userId, String userRequest) {
        // 获取用户订单信息
        List<Order> orders = orderService.getOrdersByUserId(userId);
        // 获取当前菜品信息
        List<Dish> dishes = dishService.getAllDishes();

        // 构建请求提示语
        String prompt = String.format("根据用户订单信息：%s，当前菜品信息：%s，用户要求：%s，推荐合适的菜品，以 JSON 数组形式返回，每个元素包含菜品名称（name）、价格（price）和推荐理由（reason）。",
                serializeOrders(orders), serializeDishes(dishes), userRequest);

        // 构建符合 DeepSeek API 要求的请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 200);
        requestBody.put("temperature", 0.7);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + deepSeekApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 打印请求信息
        logger.info("请求的 DeepSeek API 地址: {}", deepSeekApiUrl);
        logger.info("请求头: {}", headers);
        logger.info("请求体: {}", requestBody);

        // 调用 DeepSeek API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(deepSeekApiUrl, HttpMethod.POST, entity, String.class);

        // 解析响应
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    // 处理 Markdown 代码块
                    content = extractJsonFromMarkdown(content);
                    return objectMapper.readValue(content, List.class);
                }
            } catch (JsonProcessingException e) {
                logger.error("解析 DeepSeek API 响应失败", e);
            }
        }
        return Collections.emptyList();
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

    /**
     * 从 Markdown 代码块中提取 JSON 内容
     * @param markdown 包含 Markdown 代码块的字符串
     * @return 提取出的 JSON 字符串
     */
    private String extractJsonFromMarkdown(String markdown) {
        int startIndex = markdown.indexOf("```json");
        if (startIndex != -1) {
            startIndex += "```json".length();
            int endIndex = markdown.lastIndexOf("```");
            if (endIndex != -1) {
                return markdown.substring(startIndex, endIndex).trim();
            }
        }
        return markdown.trim();
    }
}