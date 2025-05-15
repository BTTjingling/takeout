package com.takeout.utils;

import com.takeout.utils.DeepSeekClientUtil;

import java.util.Scanner;

public class DeepSeekTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入你的 DeepSeek API Key:");
        String apiKey = scanner.nextLine();

        System.out.println("请输入提示信息:");
        String prompt = scanner.nextLine();

        try {
            String result = DeepSeekClientUtil.generateText(apiKey, prompt);
            System.out.println("生成的文本: " + result);
        } catch (Exception e) {
            System.err.println("调用 DeepSeek API 出错: " + e.getMessage());
        }

        scanner.close();
    }
}