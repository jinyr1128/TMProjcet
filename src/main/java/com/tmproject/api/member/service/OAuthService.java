package com.tmproject.api.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmproject.Common.Jwt.JwtUtil;
import com.tmproject.api.member.dto.GoogleMemberInfoDto;
import com.tmproject.api.member.dto.KakaoMemberInfoDto;
import com.tmproject.api.member.dto.NaverMemberInfoDto;
import com.tmproject.api.member.entity.Member;
import com.tmproject.api.member.entity.MemberRoleEnum;
import com.tmproject.api.member.entity.OauthEnum;
import com.tmproject.api.member.repository.MemberRepository;
import com.tmproject.global.common.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@RequiredArgsConstructor
@Slf4j(topic = "OAuth Login")
@Service
public class OAuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${kakao.client_id}")
    private String KAKAO_CLINET_ID;

    @Value("${kakao.redirect_uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${naver.client_id}")
    private String NAVER_CLIENT_ID;

    @Value("${naver.client_secret}")
    private String NAVER_CLIENT_SECRET;

    @Value("${naver.redirect_uri}")
    private String NAVER_REDIRECT_URI;

    @Value("${google.client_id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${google.client_secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${google.redirect_uri}")
    private String GOOGLE_REDIRECT_URI;

    public ApiResponseDto<?> oauthLogin(String code, String state, String oauthType) throws JsonProcessingException {
        log.info("oauthLogin() start!");
        switch(oauthType) {
            case "KAKAO" :
                String accessKakaoToken = getToken(code, null, oauthType);
                log.info("인가 코드로 액세스 토큰 요청 : "+accessKakaoToken);

                KakaoMemberInfoDto kakaoMemberInfo = getKakaoMemberInfo(accessKakaoToken);
                log.info("토큰으로 카카오 API 호출 : 액세스 토큰으로 카카오 사용자 정보 가져오기 : "+kakaoMemberInfo.getNickname());

                Member kakaoMember = registerKakaoMemberIfNeeded(kakaoMemberInfo);
                log.info("필요시에 회원가입 : "+kakaoMember.getKakaoId());
                log.info("kakaoMember.getKakaoId() : "+kakaoMember.getKakaoId());
                log.info("kakaoMember.getKakaoUsername() : "+kakaoMember.getUsername());

                // 여기서 멤버가 만들어지지 않으면?
                // 유저 네임으로 할지, id로 할지
                if(kakaoMember.getId() == null){
                    return new ApiResponseDto<>("kakao oauth 회원 가입 실패", 400, null);
                }

                String createKakaoToken =  jwtUtil.createToken(kakaoMember.getUsername(), kakaoMember.getRole());
                log.info("JWT 토큰 반환 : "+createKakaoToken);
                return new ApiResponseDto<>("Kakao oauth 로그인 성공",200, createKakaoToken);
            case "NAVER" :
                String accessNaverToken = getToken(code, state, oauthType);
                log.info("인가 코드로 액세스 토큰 요청 : " + accessNaverToken);

                NaverMemberInfoDto naverMemberInfo = getNaverMemberInfo(accessNaverToken);
                log.info("토큰으로 네이버 API 호출 : 액세스 토큰으로 네이버 사용자 정보 가져오기 : " + naverMemberInfo.getNickname());

                Member naverMember = registerNaverUserIfNeeded(naverMemberInfo);
                log.info("필요시에 회원가입 : " + naverMember.getNaverId());
                log.info("naverMember.getNaverId() : " + naverMember.getNaverId());
                log.info("naverMember.getNaverUsername() : " + naverMember.getUsername());

                if(naverMember.getId() == null){
                    return new ApiResponseDto<>("naver oauth 회원 가입 실패", 400, null);
                }

                String createNaverToken = jwtUtil.createToken(naverMember.getUsername(), naverMember.getRole());
                log.info("JWT 토큰 반환 : " + createNaverToken);

                return new ApiResponseDto<>("Naver oauth 로그인 성공",200, createNaverToken);
            case "GOOGLE" :
                String accessGoogleToken = getToken(code, null, oauthType);
                log.info("인가 코드로 액세스 토큰 요청 : " + accessGoogleToken);

                GoogleMemberInfoDto googleUserInfo = getGoogleMemberInfo(accessGoogleToken);
                log.info("토큰으로 네이버 API 호출 : 액세스 토큰으로 네이버 사용자 정보 가져오기 : test");

                Member googleMember = registerGoogleMemberIfNeeded(googleUserInfo);
                log.info("필요시에 회원가입 : " + googleMember.getGoogleId());
                log.info("googleMember.getGoogleId() : " + googleMember.getGoogleId());
                log.info("googleMember.getGoogleUsername() : " + googleMember.getUsername());

                if(googleMember.getId() == null){
                    return new ApiResponseDto<>("Google oauth 회원 가입 실패", 400, null);
                }

                String createGoggleToken = jwtUtil.createToken(googleMember.getUsername(), googleMember.getRole());
                log.info("JWT 토큰 반환 : " + createGoggleToken);
                return new ApiResponseDto<>("Google oauth 로그인 성공",200, createGoggleToken);
            default :
                return new ApiResponseDto<>("oauth 로그인 실패", 400, null);
        }
    }

    private String getToken(String code, String state, String oauthType) throws JsonProcessingException{
        switch(oauthType){
            case OauthEnum.Authority.KAKAO:
                URI kakaoUri = UriComponentsBuilder
                                .fromUriString("https://kauth.kakao.com")
                                .path("/oauth/token")
                                .encode()
                                .build()
                                .toUri();

                log.info("kakaoUri : "+kakaoUri.toString());

                HttpHeaders kakaoHeaders = new HttpHeaders();
                kakaoHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
                log.info("kakaoUri : "+kakaoUri.toString());

                // HTTP Body 생성
                MultiValueMap<String, String> kakaoBody = new LinkedMultiValueMap<>();
                kakaoBody.add("grant_type", "authorization_code");
                kakaoBody.add("client_id", KAKAO_CLINET_ID);
                kakaoBody.add("redirect_uri", KAKAO_REDIRECT_URI);
                kakaoBody.add("code", code);

                RequestEntity<MultiValueMap<String, String>> kakaoRequestEntity = RequestEntity
                        .post(kakaoUri)
                        .headers(kakaoHeaders)
                        .body(kakaoBody);

                log.info("kakaoRequestEntity : "+kakaoRequestEntity);
                // HTTP 요청 보내기
                ResponseEntity<String> kakaoResponse = restTemplate.exchange(
                        kakaoRequestEntity,
                        String.class
                );
                log.info("kakaoResponse : "+kakaoResponse);

                // HTTP 응답 (JSON) -> 액세스 토큰 파싱
                JsonNode kakaoJsonNode = new ObjectMapper().readTree(kakaoResponse.getBody());
                log.info("kakaoJsonNode.get(access_token) : "+kakaoJsonNode.get("access_token"));
                return kakaoJsonNode.get("access_token").asText();

            case OauthEnum.Authority.NAVER :
                log.info("getToken(code!)");
                URI uri = UriComponentsBuilder
                        .fromUriString("https://nid.naver.com")
                        .path("/oauth2.0/token")
                        .encode()
                        .build()
                        .toUri();

                log.info("uri : "+uri.toString());
                HttpHeaders naverHeaders = new HttpHeaders();
                naverHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
                log.info("headers : "+naverHeaders.toString());

                MultiValueMap<String, String> naverBody = new LinkedMultiValueMap<>();
                naverBody.add("grant_type", "authorization_code");
                naverBody.add("client_id", NAVER_CLIENT_ID);
                naverBody.add("client_secret", NAVER_CLIENT_SECRET);
                naverBody.add("redirect_uri", NAVER_REDIRECT_URI);
                naverBody.add("code", code);

                RequestEntity<MultiValueMap<String, String>> naverRequestEntity = RequestEntity
                        .post(uri)
                        .headers(naverHeaders)
                        .body(naverBody);

                ResponseEntity<String> naverResponse = restTemplate.exchange(
                        naverRequestEntity,
                        String.class
                );

                JsonNode naverJsonNode = new ObjectMapper().readTree(naverResponse.getBody());
                return naverJsonNode.get("access_token").asText();

            case OauthEnum.Authority.GOOGLE :
                URI googleUri = UriComponentsBuilder
                        .fromUriString("https://oauth2.googleapis.com/token")
                        .encode()
                        .build()
                        .toUri();

                log.info("uri : "+googleUri.toString());
                HttpHeaders googleHeaders = new HttpHeaders();
                googleHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
                log.info("headers : "+googleHeaders.toString());

                MultiValueMap<String, String> googleBody = new LinkedMultiValueMap<>();
                googleBody.add("grant_type", "authorization_code");
                googleBody.add("client_id", GOOGLE_CLIENT_ID);
                googleBody.add("client_secret", GOOGLE_CLIENT_SECRET);
                googleBody.add("redirect_uri", GOOGLE_REDIRECT_URI);
                googleBody.add("code", code);

                RequestEntity<MultiValueMap<String, String>> googleRequestEntity = RequestEntity
                        .post(googleUri)
                        .headers(googleHeaders)
                        .body(googleBody);

                ResponseEntity<String> googleResponse = restTemplate.exchange(
                        googleRequestEntity,
                        String.class
                );

                JsonNode googleJsonNode = new ObjectMapper().readTree(googleResponse.getBody());
                return googleJsonNode.get("access_token").asText();

            default: return null;
        }
    }

    // 카카오 로직
    private KakaoMemberInfoDto getKakaoMemberInfo(String accessToken) throws JsonProcessingException {
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

    private Member registerKakaoMemberIfNeeded(KakaoMemberInfoDto kakaoMemberInfo) {
        // db에 해당 멤버의 이름이 중복되는 멤버가 존재하는지 확인
        Member kakaoMember = memberRepository.findByUsername(kakaoMemberInfo.getNickname()).orElse(null);
        // db에 해당 멤버의 이름이 중복되는 멤버가 있을 경우
        if(kakaoMember != null) {
            Member sameKakaoIdMember = memberRepository.findByKakaoId(kakaoMemberInfo.getId()).orElse(null);

        }

        return null;
        // DB 에 중복된 Kakao Id 가 있는지 확인
        /*Long kakaoId = kakaoMemberInfo.getId();
        Member kakaoMember = memberRepository.findByKakaoId(kakaoId).orElse(null);

        // kakaoId 중복 확인
        if (kakaoMember == null) {
            // 카카오 사용자 username과 동일한 username 가진 회원이 있는지 확인
            String kakaoUsername = kakaoMemberInfo.getNickname();
            // db에 같은 이름 멤버 있는지 확인
            Member sameUsernameMember = memberRepository.findByUsername(kakaoUsername).orElse(null);

            // db에 같은 이름 멤버 존재
            if(sameUsernameMember != null) {
                if(sameUsernameMember.getKakaoId() == null){
                    kakaoMember = sameUsernameMember;
                    kakaoMember = kakaoMember.kakaoIdUpdate(kakaoId);
                }else{
                    return null;
                }
            }

            // db에 같은 이름 멤버 존재하든 말든 이동
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = kakaoMemberInfo.getEmail();
            Member sameEmailMember = memberRepository.findByEmail(kakaoEmail).orElse(null);
            if (sameEmailMember != null) {
                // 중복되는 emailMember 존재
                kakaoMember = sameEmailMember;
                kakaoMember = kakaoMember.kakaoIdUpdate(kakaoId);
            } else {
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);
                String email = kakaoMemberInfo.getEmail();

                kakaoMember = new Member(kakaoMemberInfo.getNickname(), encodedPassword, email, MemberRoleEnum.USER, kakaoId);
            }

            memberRepository.save(kakaoMember);

            return kakaoMember;
        }*/
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

        GoogleMemberInfoDto googleUserInfo = GoogleMemberInfoDto.builder()
                .id(id)
                .email(email)
                .username(username).build();
        return googleUserInfo;
    }

    private Member registerGoogleMemberIfNeeded(GoogleMemberInfoDto googleMemberInfoDto) {
        String googleMemberUsername = googleMemberInfoDto.getUsername();
        String googleMemberEmail = googleMemberInfoDto.getEmail();
        String googleMemberId = googleMemberInfoDto.getId();
        Member googleMember = memberRepository.findByGoogleId(googleMemberId).orElse(null);

        if (googleMember == null) {
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            googleMember = new Member(googleMemberUsername, encodedPassword, googleMemberEmail, MemberRoleEnum.USER, googleMemberId, null);

            memberRepository.save(googleMember);
        }
        return googleMember;
    }

}
