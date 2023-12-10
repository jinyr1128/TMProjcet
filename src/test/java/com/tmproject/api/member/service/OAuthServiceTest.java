package com.tmproject.api.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmproject.Common.Jwt.JwtUtil;
import com.tmproject.api.member.repository.MemberRepository;
import com.tmproject.global.common.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.when;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class OAuthServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    OAuthService oAuthService;

    @Mock
    JwtUtil jwtUtil;

    @Spy
    private OAuthService spyOAuthService;

   /*@Test
    @DisplayName("[OAuthSerivce] oauthLogin kakaoSuccess")
    public void testOauthLoginWithKakao() throws JsonProcessingException {
        // given
        when(spyOAuthService.getToken(Mockito.anyString(), Mockito.isNull(), Mockito.eq("KAKAO"))).thenReturn("mocked_access_token");
        when(jwtUtil.createToken(Mockito.anyString(), Mockito.any())).thenReturn("mocked_jwt_token");

        // when
        ApiResponseDto<?> response = oAuthService.oauthLogin("mocked_code", null, "KAKAO");
    }

    @Test
    @DisplayName("[OAuthSerivce] oauthLogin naverSuccess")
    public void testOauthLoginWithNaver() throws JsonProcessingException {

    }

    @Test
    @DisplayName("[OAuthSerivce] oauthLogin googleSuccess")
    public void testOauthLoginWithGoogle() throws JsonProcessingException {

    }*/

}