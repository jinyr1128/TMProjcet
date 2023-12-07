package com.tmproject.api.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmproject.Common.Jwt.JwtUtil;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.entity.MemberRoleEnum;
import com.tmproject.api.member.repository.MemberRepository;
import com.tmproject.api.member.dto.KakaoMemberInfoDto;
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

@Slf4j(topic = "KAKAO Login")
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    // spring boot에서 수동으로 빈 등록해서 관리를 유도함
    // 추가적인 설정을 한 RestTemplate
    private final JwtUtil jwtUtil;

    private static String kakao_client_id = "8bbcb11360f192ffb599e80a0fc3489a";

    public String kakaoLogin(String code) throws JsonProcessingException {
        String accessToken = getToken(code);
        log.info("인가 코드로 액세스 토큰 요청 : "+accessToken);

        KakaoMemberInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        log.info("토큰으로 카카오 API 호출 : 액세스 토큰으로 카카오 사용자 정보 가져오기 : "+kakaoUserInfo.getNickname());

        Member kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);
        log.info("필요시에 회원가입 : "+kakaoUser.getKakaoId());
        log.info("kakaoUser.getKakaoId() : "+kakaoUser.getKakaoId());
        log.info("kakaoUser.getKakaoUsername() : "+kakaoUser.getUsername());

        String createToken =  jwtUtil.createToken(kakaoUser.getUsername(), kakaoUser.getRole());
        log.info("JWT 토큰 반환 : "+createToken);
        return createToken;
    }

    private String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        // 요청 uri = "https://kauth.kakao.com/oauth/token"
        log.info("인가 코드 : "+code);
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        // Http Header에 데이터 유형 Content-type, application/x-www-form-urlencoded; charset=utf-8
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakao_client_id);
        body.add("redirect_uri", "http://localhost:8080/api/member/kakao/callback");
        body.add("code", code);
        // 'https://kauth.kakao.com/oauth/authorize?client_id=8bbcb11360f192ffb599e80a0fc3489a
        // &redirect_uri=http://localhost:8080/api/member/kakao/callback&response_type=code'
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText();
    }

    private KakaoMemberInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoMemberInfoDto(id, nickname, email);
    }

    private Member registerKakaoUserIfNeeded(KakaoMemberInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        Member kakaoUser = memberRepository.findByKakaoId(kakaoId).orElse(null);

        if (kakaoUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = kakaoUserInfo.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                String email = kakaoUserInfo.getEmail();

                kakaoUser = new Member(kakaoUserInfo.getNickname(), encodedPassword, email, MemberRoleEnum.USER, kakaoId);
            }

            memberRepository.save(kakaoUser);
        }
        return kakaoUser;
    }
}