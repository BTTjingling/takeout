package com.takeout.utils;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeepSeekClientUtil {

    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final Logger LOGGER = Logger.getLogger(DeepSeekClientUtil.class.getName());

    /**
     * 使用 DeepSeek API 生成文本
     * @param apiKey DeepSeek API Key
     * @param prompt 输入的提示信息
     * @return 生成的文本结果
     * @throws IOException 发送请求时出现 IO 异常
     */
    public static String generateText(String apiKey, String prompt) throws IOException {
        // 根据官方文档修改为可用模型名称
        String requestBody = gson.toJson(new ChatRequest("deepseek-chat", prompt));
        LOGGER.log(Level.INFO, "Request Body: {0}", requestBody);

        // 构建请求
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();

        // 发送请求
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                ResponseBody errorBody = response.body();
                if (errorBody != null) {
                    String errorMessage = errorBody.string();
                    LOGGER.log(Level.SEVERE, "Error Response: {0}", errorMessage);
                }
                throw new IOException("Unexpected code " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseData = responseBody.string();
                LOGGER.log(Level.INFO, "Response Data: {0}", responseData);
                ChatResponse chatResponse = gson.fromJson(responseData, ChatResponse.class);
                // 检查 choices 数组是否为空
                if (chatResponse.choices != null && chatResponse.choices.length > 0) {
                    return chatResponse.choices[0].message.content;
                }
            }
        }
        return "";
    }

    // 请求体类
    static class ChatRequest {
        String model;
        Message[] messages;

        ChatRequest(String model, String prompt) {
            this.model = model;
            this.messages = new Message[]{new Message("user", prompt)};
        }
    }

    // 消息类
    static class Message {
        String role;
        String content;

        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    // 响应类
    static class ChatResponse {
        Choice[] choices;
    }

    // 选项类
    static class Choice {
        Message message;
    }
}