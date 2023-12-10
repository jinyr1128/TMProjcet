package com.tmproject.api.member.service;

import com.tmproject.Common.Jwt.JwtUtil;
import com.tmproject.api.member.repository.MemberRepository;
import com.tmproject.global.common.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
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

    @Test
    public void testOauthLoginWithKakao() throws Exception {
        // Given
        when(spyOAuthService.getToken(Mockito.anyString(), Mockito.isNull(), Mockito.eq("KAKAO"))).thenReturn("mocked_access_token");
        //when(spyOAuthService.getKakaoMemberInfo(Mockito.anyString())).thenReturn(new KakaoMemberInfoDto("mocked_nickname"));
        //when(oAuthService.registerKakaoMemberIfNeeded(Mockito.any())).thenReturn(new Member("mocked_kakao_id", "mocked_kakao_username"));
        when(jwtUtil.createToken(Mockito.anyString(), Mockito.any())).thenReturn("mocked_jwt_token");

        // When
        ApiResponseDto<?> response = oAuthService.oauthLogin("mocked_code", null, "KAKAO");
    }

    @Test
    public void testOauthLoginWithNaver() throws Exception {
        // Naver에 대한 테스트도 비슷하게 작성할 수 있습니다.
    }

    @Test
    public void testOauthLoginWithGoogle() throws Exception {
        // Google에 대한 테스트도 비슷하게 작성할 수 있습니다.
    }

    @Test
    public void testOauthLoginWithInvalidProvider() throws Exception {

    }
}