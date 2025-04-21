package com.takeout.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // 全参构造器
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 快速构建成功响应
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    // 快速构建失败响应
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
