package com.tmproject.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoMemberInfoDto {
    private Long id;
    private String nickname;
    private String email;
}
