package com.example.HW18.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
