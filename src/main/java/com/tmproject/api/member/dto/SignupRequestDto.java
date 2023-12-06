package com.tmproject.api.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupRequestDto {

    @NotNull
    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "Invalid username")
    private String username;
    // username은  최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 한다.

    @NotNull
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$", message = "Invalid password")
    private String password;
    // password는  최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로 구성되어야 한다.

    @NotNull
    @Email
    private String email;
}
