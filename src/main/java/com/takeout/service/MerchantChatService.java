package com.takeout.service;

public interface MerchantChatService {
    /**
     * 根据商家 ID 和商家问题获取 AI 聊天回复
     * @param merchantId 商家 ID
     * @param question 商家提出的问题
     * @return AI 聊天回复内容
     */
    String getChatResponse(Long merchantId, String question);
}