package com.takeout.controller;

import com.takeout.service.AIService;
import com.takeout.service.MerchantChatService;
import com.takeout.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/generate-text")
    public String generateText(@RequestBody String prompt) throws IOException {
        return aiService.generateText(prompt);
    }

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/recommend-dishes")
    public List<Map<String, Object>> recommendDishes(@RequestParam Long userId, @RequestParam String userRequest) {
        return recommendationService.getRecommendations(userId, userRequest);
    }
    @Autowired
    private MerchantChatService merchantChatService;

    @GetMapping("/merchant-chat")
    public String chatWithAssistant(@RequestParam Long merchantId, @RequestParam String question) {
        return merchantChatService.getChatResponse(merchantId, question);
    }
}
