package com.tmproject.api.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmproject.Common.Jwt.JwtUtil;
import com.tmproject.api.member.dto.NaverMemberInfoDto;
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

@Slf4j(topic = "NAVER Login")
@RequiredArgsConstructor
@Service
public class NaverService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    private static String naver_client_id = "3jo14itcz2ELeaKj8BPQ";
    private static String naver_client_secret = "DUp0cF33Dw";
    // https://nid.naver.com/oauth2.0/authorize?client_id=3jo14itcz2ELeaKj8BPQ
    // &response_type=code&redirect_uri=http://localhost:8080/api/member/naver/callback&state={상태 토큰}
    public String naverLogin(String code, String state) throws JsonProcessingException {
        String accessToken = getToken(code);
        log.info("인가 코드로 액세스 토큰 요청 : " + accessToken);

        NaverMemberInfoDto naverMemberInfo = getNaverMemberInfo(accessToken);
        log.info("토큰으로 네이버 API 호출 : 액세스 토큰으로 네이버 사용자 정보 가져오기 : " + naverMemberInfo.getNickname());

        Member naverUser = registerNaverUserIfNeeded(naverMemberInfo);
        log.info("필요시에 회원가입 : " + naverUser.getNaverId());
        log.info("naverUser.getNaverId() : " + naverUser.getNaverId());
        log.info("naverUser.getNaverUsername() : " + naverUser.getUsername());

        String createToken = jwtUtil.createToken(naverUser.getUsername(), naverUser.getRole());
        log.info("JWT 토큰 반환 : " + createToken);
        return createToken;
    }


    private String getToken(String code) throws JsonProcessingException {
        log.info("getToken(code!)");
        URI uri = UriComponentsBuilder
                .fromUriString("https://nid.naver.com")
                .path("/oauth2.0/token")
                .encode()
                .build()
                .toUri();

        log.info("uri : "+uri.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        log.info("headers : "+headers.toString());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", naver_client_id);
        body.add("client_secret", naver_client_secret);
        body.add("redirect_uri", "http://localhost:8080/api/member/naver/callback");
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

    private NaverMemberInfoDto getNaverMemberInfo(String accessToken) throws JsonProcessingException {
        log.info("getNaverMemberInfo() start! ");
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/nid/me")
                .encode()
                .build()
                .toUri();
        log.info("uri : "+uri.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        log.info("jsonNode.get(response) : "+jsonNode.get("response"));
        String naverName = jsonNode.get("response").get("name").asText();
        String naverEmail = jsonNode.get("response").get("email").asText();
        log.info("nickname : "+naverName);
        log.info("email : "+naverEmail);
        String naverId = jsonNode.get("response").get("id").asText();

        log.info("네이버 사용자 정보: " + naverId + ", " + naverName + ", " + naverEmail);
        return new NaverMemberInfoDto(naverId, naverName, naverEmail);
    }

    private Member registerNaverUserIfNeeded(NaverMemberInfoDto naverMemberInfo) {
        String naverId = naverMemberInfo.getId();
        Member naverUser = memberRepository.findByNaverId(naverId).orElse(null);

        if (naverUser == null) {
            String naverEmail = naverMemberInfo.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(naverEmail).orElse(null);
            if (sameEmailUser != null) {
                naverUser = sameEmailUser;
                naverUser = naverUser.naverIdUpdate(naverId);
            } else {
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);
                String email = naverMemberInfo.getEmail();

                naverUser = new Member(naverMemberInfo.getNickname(), encodedPassword, email, MemberRoleEnum.USER, naverId);
            }

            memberRepository.save(naverUser);
        }
        return naverUser;
    }
}
