package com.tmproject.api.member.dto;

import com.tmproject.api.member.entity.Member;
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

    public ProfileResponseDto(Member member){
        this.username = member.getUsername();
        this.profileImageUrl = member.getProfileImageUrl();
        this.introduction = member.getIntroduction();
    }
}
