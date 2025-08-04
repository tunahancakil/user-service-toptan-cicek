package com.ttsoftware.userservice.domain.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String userName;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
