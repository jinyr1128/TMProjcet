package com.tmproject.Common.Security;

import com.tmproject.Common.Jwt.JwtAuthenticationFilter;
import com.tmproject.Common.Jwt.JwtAuthorizationFilter;
import com.tmproject.Common.Jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final MemberDetailsServiceImpl memberDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, memberDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("/api/**").permitAll() // 테스트를 위해 먼저 성공 되는지 확인
                        .requestMatchers("/api/member/boards/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/member/profile/**").permitAll()
                        .requestMatchers("/api/member/login","/api/member/loginPage").permitAll()
                        .requestMatchers("/api/member/signup").permitAll()
                        .requestMatchers("/api/member/logout").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/api/member/profile/**").authenticated()
                        .requestMatchers("/api/member/profile/**/imageProfileUrl").authenticated()
                        .requestMatchers("/api/member/comments/**").authenticated()
                        .requestMatchers("/api/member/kakao/callback").permitAll()
                        .requestMatchers("/api/member/naver/callback").permitAll()
                        .requestMatchers("/api/member/google/callback").permitAll()
                        .requestMatchers("/api/member/**/follow").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/member/boards").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/member/boards").authenticated()
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리, /api/member/boards/** 요청도 인증이 필요해
        );

        http.formLogin((formLogin) ->
                formLogin
                        .loginPage("/api/member/loginPage").permitAll()
                        .defaultSuccessUrl("/homePage")
                // loginPage 임시 지정
        ).logout(logout ->
                logout
                        .logoutUrl("/api/member/logout")
                        .logoutSuccessUrl("/api/member/loginPage")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
        );

        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}