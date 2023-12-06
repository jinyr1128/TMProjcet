package com.tmproject.api.member.entity;

import com.tmproject.api.member.dto.ProfileRequestDto;
import com.tmproject.global.common.Timestamped;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
    public void update(ProfileRequestDto profileRequestDto, String encodedPassowrd){
        this.username = profileRequestDto.getUsername();
        this.password = encodedPassowrd;
        this.email = profileRequestDto.getEmail();
        this.introduction = profileRequestDto.getIntroduction();
        // 회원 수정
    }

    public void updateProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
        // 이거 임시일듯? 잠시,,, 테스트좀 해봐야해
    }
    // 따로 이미지파일url 변경 로직도 필요할듯?
}