package com.tmproject.api.member.entity;

import com.tmproject.api.member.dto.ProfileResponseDto;
import com.tmproject.api.member.dto.ProfileUpdateRequestDto;
import com.tmproject.global.common.Timestamped;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long kakaoId;
    private String naverId;

    @Column(nullable = false)
    private String username;
    // username은  최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 한다.

    @Column
    @NotNull
    private String password;
    // password는  최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로 구성되어야 한다.

    @Column
    @NotNull
    private String email;
    // 회원가입 시 email

    @Column
    private String profileImageUrl;
    // 프로필 사진
    // localStorage 사용해서 넣는 방법으로 실행
    // 추후 cloud 사용 가능성 높음

    private String introduction;
    // 자기 소개

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberRoleEnum role;

    public Member(String username, String password, String email, MemberRoleEnum role){
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        // 기본 정보 저장
    }

    public Member(String username, String password, String email, MemberRoleEnum role, Long kakaoId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.kakaoId = kakaoId;
    }

    public Member(String username, String password, String email, MemberRoleEnum role, String naverId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.naverId = naverId;
    }

    public void update(ProfileUpdateRequestDto profileRequestDto, String encodedPassowrd){
        this.username = profileRequestDto.getUsername();
        this.password = encodedPassowrd;
        this.email = profileRequestDto.getEmail();
        this.introduction = profileRequestDto.getIntroduction();
        // 회원 수정
    }

    public void updateProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }

    public Member kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public Member naverIdUpdate(String naverId) {
        this.naverId = naverId;
        return this;
    }
}