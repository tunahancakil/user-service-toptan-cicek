package com.ttsoftware.userservice.domain.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String usernameOrEmail;
    private String password;
    private Integer channelId;
}
