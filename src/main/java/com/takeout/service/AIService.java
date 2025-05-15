package com.takeout.service;

import java.io.IOException;


public interface AIService {
    String generateText(String prompt) throws IOException;
}

