package com.ttsoftware.userservice.domain.dto;

import lombok.Data;

@Data
public class SignupDto {
    private String email;
    private String name;
    private String surname;
    private String password;
    private String username;
    private Integer channelId;
}
