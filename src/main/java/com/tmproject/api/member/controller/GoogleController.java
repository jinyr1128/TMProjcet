package com.tmproject.api.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmproject.Common.Jwt.JwtUtil;
import com.tmproject.api.member.service.GoogleService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/api")
public class GoogleController {
    private final GoogleService googleService;

    @GetMapping("/member/google/callback")
    public String googleLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        log.info("code : "+code);
        //log.info("state : "+state);
        String token = googleService.googleLogin(code);
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/";
    }
}
