package com.tmproject.api.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmproject.Common.Jwt.JwtUtil;
import com.tmproject.api.member.service.NaverService;
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
public class NaverController {
    private final NaverService naverService;

    @GetMapping("/member/naver/callback")
    public String NaverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException {
        log.info("code : "+code);
        log.info("state : "+state);
        String token = naverService.naverLogin(code, state);
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/";
    }
}




    /* application.yml
      security:
        oauth2:
          client:
            registration:
              naver:
                client-id: 3jo14itcz2ELeaKj8BPQ
                client-secret: DUp0cF33Dw
                redirect-uri: {baseUrl}/{action}/oauth2/code/{registrationId}
                authorization-grant-type: authorization_code
                scope: name, email
                client-name: Naver
                # Naver Spring Security 수동 입력
        provider:
          naver :
            authorization-uri: https://nid.naver.com/oauth2.0/authorize
            token-uri: https://nid.naver.com/oauth2.0/token
            user-info-uri: //openapi.naver.com/v1/nid/me */
