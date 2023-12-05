package com.tmproject.api.member.entity;

import com.tmproject.api.member.dto.ProfileRequestDto;
import com.tmproject.global.common.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "Invalid username")
    private String username;
    // username은  최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 한다.

    @Column
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$", message = "Invalid password")
    private String password;
    // password는  최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자로 구성되어야 한다.

    @Column
    private String email;
    // 회원가입 시 email

    @Column
    private String profileImageUrl;
    // 프로필 사진

    private String introduction;
    // 자기 소개

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberRoleEnum role;

    public Member(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
        // 기본 정보 저장
    }
    public void update(ProfileRequestDto profileRequestDto){
        this.username = profileRequestDto.getUsername();
        this.password = profileRequestDto.getPassword();
        this.email = profileRequestDto.getEmail();
        this.introduction = profileRequestDto.getIntroduction();
        this.profileImageUrl = profileRequestDto.getProfileImageUrl();
        // 회원 수정
    }

}
