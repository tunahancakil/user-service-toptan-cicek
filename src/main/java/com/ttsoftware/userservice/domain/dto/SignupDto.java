package com.ttsoftware.userservice.domain.dto;

import lombok.Data;

@Data
public class SignupDto {
    private String name;
    private String email;
    private String surname;
    private String password;
    private String phone;
    private String phoneNumber;
    private String country;
    private String city;
    private String address;
    private String dealerName;
    private String taxNumber;
    private String taxOffice;
    private boolean isDealer;
}
