package com.takeout.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private Long shopId;
    private String oldPassword;
    private String newPassword;
}