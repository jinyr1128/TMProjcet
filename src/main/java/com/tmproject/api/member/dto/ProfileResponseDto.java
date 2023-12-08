package com.tmproject.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ProfileResponseDto {
    private String username;
    private String introduction;
    private String profileImageUrl;

}
