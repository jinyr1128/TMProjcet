package com.tmproject.Common.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.member.dto.LoginRequestDto;
import com.tmproject.api.member.entity.MemberRoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/member/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("attemptAuthentication()");
        log.info("request : "+request);
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        log.info("successfulAuthentication()");
        String username = ((MemberDetailsImpl) authResult.getPrincipal()).getUsername();
        MemberRoleEnum role = ((MemberDetailsImpl) authResult.getPrincipal()).getMember().getRole();

        String token = jwtUtil.createToken(username, role);
        log.info(token);
        // 토큰 요청할 때 잘못 보내는거 같음,
        response.setHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        // response.setHeader(); 없을 때 넣어주는데, 중복된 토큰이 있으면 업데이트 해준다.
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }

}
