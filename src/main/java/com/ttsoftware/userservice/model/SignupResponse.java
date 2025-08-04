package com.ttsoftware.userservice.model;

import lombok.Data;

@Data
public class SignupResponse {
    private Integer status;
    private String message;
    private Integer errorCode;
    private String errorMessage;
}
