package com.takeout.service;

import java.util.List;
import java.util.Map;

public interface RecommendationService {
    /**
     * 根据用户 ID 和用户要求获取推荐菜品
     * @param userId 用户 ID
     * @param userRequest 用户要求
     * @return 推荐菜品列表
     */
    List<Map<String, Object>> getRecommendations(Long userId, String userRequest);
}
