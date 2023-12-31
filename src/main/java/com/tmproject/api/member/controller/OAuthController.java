package com.tmproject.api.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmproject.Common.Jwt.JwtUtil;
import com.tmproject.api.member.entity.OauthEnum;
import com.tmproject.api.member.service.OAuthService;
import com.tmproject.global.common.ApiResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j(topic = "OAuth Controller")
@RestController
@RequestMapping("/api/member")
public class OAuthController {
    private final OAuthService oauthService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<ApiResponseDto<?>> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        logCode(code, null);
        ApiResponseDto<?> apiResponseDto = oauthService.oauthLogin(code, null, OauthEnum.KAKAO.getAuthority());
        String token = apiResponseDto.getData().toString();
        makeCookie(token, response);
        return new ResponseEntity<>(apiResponseDto, HttpStatus.valueOf(apiResponseDto.getStatusCode()));
    }

    @GetMapping("/naver/callback")
    public ResponseEntity<ApiResponseDto<?>> naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException{
        logCode(code, null);
        ApiResponseDto<?> apiResponseDto = oauthService.oauthLogin(code, state, OauthEnum.NAVER.getAuthority());
        String token = apiResponseDto.getData().toString();
        makeCookie(token, response);
        return new ResponseEntity<>(apiResponseDto, HttpStatus.valueOf(apiResponseDto.getStatusCode()));
    }

    @GetMapping("/google/callback")
    public ResponseEntity<ApiResponseDto<?>> googleLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException{
        logCode(code, null);
        ApiResponseDto<?> apiResponseDto = oauthService.oauthLogin(code, null, OauthEnum.GOOGLE.getAuthority());
        String token = apiResponseDto.getData().toString();
        makeCookie(token, response);
        return new ResponseEntity<>(apiResponseDto, HttpStatus.valueOf(apiResponseDto.getStatusCode()));
    }

    private void logCode(String code, String state){
        log.info("OAuth Authorization code : "+code);
        log.info("state : "+state);
    }

    private void makeCookie(String token, HttpServletResponse response){
        // 해당 토큰을 ApiResponseDto에서받아야 할듯?
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    // authorization code -> jwt token으로
    // 네이버 인가 코드를 header로 받아볼 수 있게 만들어볼래?~ 라네요?
    // 이거 컨트롤러...로? 만들라는데,, 어떻게 하는지 몰라요

    // 1. oauthService db unqiue key 중복 해결하기
    // 2. custom jwt Token rendering

}
