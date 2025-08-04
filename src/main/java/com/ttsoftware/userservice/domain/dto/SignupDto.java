package com.ttsoftware.userservice.domain.dto;

import lombok.Data;

@Data
public class SignupDto {
    private String name;
    private String email;
    private String surname;
    private String password;
}
