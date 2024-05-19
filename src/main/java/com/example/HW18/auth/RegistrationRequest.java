package com.example.HW18.auth;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String email;
    private String password;
    private String name;
    private int age;
}
