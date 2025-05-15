package com.takeout.service.impl;

import com.takeout.service.AIService;
import com.takeout.utils.DeepSeekClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AIServiceImpl implements AIService {

    @Value("${deepseek.api.key}") // 从配置文件中读取 API Key
    private String apiKey;

    @Override
    public String generateText(String prompt) throws IOException {
        return DeepSeekClientUtil.generateText(apiKey, prompt);
    }
}
