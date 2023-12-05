package com.tmproject.Common.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmproject.Common.Security.MemberDetailsImpl;
import com.tmproject.api.member.dto.LoginRequestDto;
import com.tmproject.api.member.entity.MemberRoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "JwtAuthenticationFilter 관련 로그")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/member/login");
        // login process -> /api/member/login
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginRequestDto loginDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            log.info("loginDto.getUsername() : "+loginDto.getUsername()+" || loginDto.getPassword() : "+loginDto.getPassword());
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword(),
                            null
                    )
                    // 권한 null로 지정, 추후 변경 가능성 있음
            );
        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // 인증 성공(로그인 성공) 시 동작하는 메서드
    // JWT 생성 및 client에 응답
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        log.info("로그인 성공 및 JWT 생성");
        String username = ((MemberDetailsImpl) authResult.getPrincipal()).getUsername();
        MemberRoleEnum role = ((MemberDetailsImpl) authResult.getPrincipal()).getMember().getRole();
        String token = jwtUtil.createToken(username, role);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
    }

    //인증 실패(로그인 실패)시 동작하는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401);
    }
}
