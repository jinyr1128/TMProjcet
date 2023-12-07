package com.tmproject.api.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmproject.Common.Jwt.JwtUtil;
import com.tmproject.api.member.dto.GoogleMemberInfoDto;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.entity.MemberRoleEnum;
import com.tmproject.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "Google Login")
@RequiredArgsConstructor
@Service
public class GoogleService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    private static String google_client_id = "807257127729-2eg30f8m4cq2ngtm1d6q4o2o68p5q8bm.apps.googleusercontent.com";
    private static String google_client_secret = "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";
        // https://accounts.google.com/o/oauth2/auth?client_id=807257127729-2eg30f8m4cq2ngtm1d6q4o2o68p5q8bm.apps.googleusercontent.com
    // &redirect_uri=http://localhost:8080/api/member/google/callback&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile
    public String googleLogin(String code) throws JsonProcessingException {
        String accessToken = getToken(code);
        log.info("인가 코드로 액세스 토큰 요청 : " + accessToken);

        GoogleMemberInfoDto googleUserInfo = getGoogleMemberInfo(accessToken);
        log.info("토큰으로 네이버 API 호출 : 액세스 토큰으로 네이버 사용자 정보 가져오기 : test");

        Member googleMember = registerGoogleMemberIfNeeded(googleUserInfo);
        log.info("필요시에 회원가입 : " + googleMember.getGoogleId());
        log.info("googleMember.getNaverId() : " + googleMember.getGoogleId());
        log.info("googleMember.getNaverUsername() : " + googleMember.getUsername());

    String createToken = jwtUtil.createToken(googleMember.getUsername(), googleMember.getRole());
        log.info("JWT 토큰 반환 : " + createToken);
        return createToken;
}


    private String getToken(String code) throws JsonProcessingException {
        log.info("getToken(code!)");
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com/token")
                .encode()
                .build()
                .toUri();

        log.info("uri : "+uri.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        log.info("headers : "+headers.toString());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", google_client_id);
        body.add("client_secret", google_client_secret);
        body.add("redirect_uri", "http://localhost:8080/api/member/google/callback");
        body.add("code", code);
        //Cannot invoke "com.fasterxml.jackson.databind.JsonNode.asLong()" because the return value of "com.fasterxml.jackson.databind.JsonNode.get(String)" is null] with root cause
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    private GoogleMemberInfoDto getGoogleMemberInfo(String accessToken) throws JsonProcessingException {
        log.info("getGoogleMemberInfo() start! ");
        URI uri = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/oauth2/v3/tokeninfo")
                .queryParam("access_token", accessToken).encode()
                .build()
                .toUri();

        log.info("uri : "+uri.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        //headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        log.info("jsonNode.get(id) : "+jsonNode.get("sub"));
        log.info("jsonNode.get(email) : "+jsonNode.get("email"));
        log.info("jsonNode.get(exp) : "+jsonNode.get("exp"));
        // 이메일은 존재하네?
        String id = jsonNode.get("sub").asText();
        // sub : 요청을 수행하는 주 구성원을 나타내는 ID입니다.
        String email = jsonNode.get("email").asText();
        String[] parts = email.split("@");
        // @를 기준으로 문자열 분할, @ 앞의 부분 추출
        String username = parts[0];
        log.info("Username: " + username);
        // username, id가 존재하지 않네?
        GoogleMemberInfoDto googleUserInfo = GoogleMemberInfoDto.builder()
                .id(id)
                .email(email)
                .username(username).build();
        return googleUserInfo;
    }

    private Member registerGoogleMemberIfNeeded(GoogleMemberInfoDto googleMemberInfoDto) {
        String googleMemberNickname = googleMemberInfoDto.getUsername();
        String googleMemberEmail = googleMemberInfoDto.getEmail();
        String googleMemberId = googleMemberInfoDto.getId();
        Member googleMember = memberRepository.findByGoogleId(googleMemberId).orElse(null);

        if (googleMember == null) {
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            googleMember = new Member(googleMemberNickname, encodedPassword, googleMemberEmail, MemberRoleEnum.USER, googleMemberId, null);

            memberRepository.save(googleMember);
        }
        return googleMember;
    }
}
