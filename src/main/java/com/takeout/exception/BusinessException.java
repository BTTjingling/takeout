package com.takeout.exception;

// 自定义业务异常
public class BusinessException extends RuntimeException {
    private String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
