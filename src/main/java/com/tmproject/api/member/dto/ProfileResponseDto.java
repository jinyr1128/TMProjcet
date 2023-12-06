package com.tmproject.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProfileResponseDto {
    private String username;
    private String introduction;

}
