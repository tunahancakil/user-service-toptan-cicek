package com.ttsoftware.userservice.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String username;
    private LocalDateTime createdDate;
}
