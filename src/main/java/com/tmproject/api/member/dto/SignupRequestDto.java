package com.tmproject.api.member.dto;

import jakarta.validation.constraints.Pattern;

public class SignupRequestDto {
    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "Invalid username")
    private String username;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$", message = "Invalid password")
    private String password;
    private String eamil;
}
