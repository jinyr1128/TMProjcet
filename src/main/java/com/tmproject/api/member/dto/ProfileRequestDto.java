package com.tmproject.api.member.dto;

import lombok.Getter;

@Getter
public class ProfileRequestDto {
    private String username;
    private String password;
    private String passwordConfirm;
    private String email;
    private String introduction;
    private String profileImageUrl;
}
