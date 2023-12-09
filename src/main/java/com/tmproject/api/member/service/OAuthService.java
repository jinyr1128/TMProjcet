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
import org.springframework.transaction.annotation.Transactional;
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
                //kakaoMember.
                // 여기서 멤버가 만들어지지 않으면?
                // 유저 네임으로 할지, id로 할지
                if(kakaoMember.getId() == null){
                    return new ApiResponseDto<>("kakao oauth 회원 가입 실패", 400, null);
                }
                log.info("필요시에 회원가입 : "+kakaoMember.getKakaoId());
                log.info("kakaoMember.getKakaoId() : "+kakaoMember.getKakaoId());
                log.info("kakaoMember.getKakaoUsername() : "+kakaoMember.getUsername());

                String createKakaoToken =  jwtUtil.createToken(kakaoMember.getUsername(), kakaoMember.getRole());
                log.info("JWT 토큰 반환 : "+createKakaoToken);
                return new ApiResponseDto<>("Kakao oauth 로그인 성공",200, createKakaoToken);
                // 토큰 가지고 kakao api info정보 얻기 -> accessToken으로
                // kakao
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

                log.info("naverResponse : "+naverResponse);

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
                log.info(googleJsonNode.toPrettyString());
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

    @Transactional
    public Member registerKakaoMemberIfNeeded(KakaoMemberInfoDto kakaoMemberInfo) {
        // KakaoMemberInfoDto -> id, nickname, email
        // 해당 카카오 아이디 확인하기
        // 검증해야하는 email, username, id를 전부다
        log.info("registerKakaoMemberIfNeeded() 검증 시작");
        Long kakaoId = kakaoMemberInfo.getId();

        log.info("kakaoMemberInfo.getId() : "+kakaoMemberInfo.getId());
        log.info("kakaoMemberInfo.getEmail() : "+kakaoMemberInfo.getEmail());
        log.info("kakaoMemberInfo.getNickname() : "+kakaoMemberInfo.getNickname());

        Member duplicateMember = memberRepository
                .findByKakaoIdOrEmailOrUsername(
                        kakaoMemberInfo.getId(),
                        kakaoMemberInfo.getEmail(),
                        kakaoMemberInfo.getNickname()
                ).orElse(null);

        if(duplicateMember == null){
            log.info("중복되는 kakaoId, username, email없을때 새로운 계정 회원 가입");
            String uuid = UUID.randomUUID().toString();
            Member member = Member.builder()
                    .username(kakaoMemberInfo.getNickname())
                    .password(uuid)
                    .email(kakaoMemberInfo.getEmail())
                    .role(MemberRoleEnum.USER)
                    .kakaoId(kakaoMemberInfo.getId())
                    .build();

            memberRepository.save(member);
            return member;
        }else{
            log.info("username, email, kakaoId 셋 중 하나 이상이 중복될 때");
            log.info("kakaoMemberInfo.getId() : "+duplicateMember.getId());
            log.info("kakaoMemberInfo.getEmail() : "+duplicateMember.getEmail());
            log.info("kakaoMemberInfo.getNickname() : "+duplicateMember.getUsername());

            Member kakaoIdMember = memberRepository.findByKakaoId(kakaoMemberInfo.getId()).orElse(null);

            // kakaoId를 가진 멤버가 없으면
            if(kakaoIdMember == null){
                log.info("kakaoId가 가진 멤버가 아닌 username, email 둘 중 하나가 중복될 때");
                log.info("구글 아이디, 네이버 아이디 탐색하기");
                String naverId = duplicateMember.getNaverId();
                String googleId = duplicateMember.getGoogleId();

                if (naverId != null) {
                    if (googleId != null) {
                        // 둘다 가지는 멤버
                        log.info("naverId와 googleId를 둘 다 가지는 멤버");
                        log.info("kakaoId : "+duplicateMember.getKakaoId());
                        log.info("naverId : "+naverId);
                        log.info("googleId : "+googleId);
                        duplicateMember.OauthIdUpdate(kakaoId, naverId, googleId);
                    } else {
                        // naverId만 가지는 멤버
                        log.info("naverId만 가지는 멤버");
                        log.info("kakaoId : "+duplicateMember.getKakaoId());
                        log.info("naverId : "+naverId);
                        duplicateMember.OauthIdUpdate(kakaoId, naverId, null);
                        log.info("kakaoId : "+duplicateMember.getKakaoId());
                    }
                } else {
                    // naverId를 가지지 않는 멤버
                    if (googleId == null) {
                        log.info("둘 다 가지지 않는 멤버");
                        duplicateMember.OauthIdUpdate(kakaoId, null, null);
                    } else {
                        log.info("googleId만 가지는 멤버");
                        log.info("kakaoId : "+duplicateMember.getKakaoId());
                        log.info("googleId : "+googleId);
                        duplicateMember.OauthIdUpdate(kakaoId, null, googleId);
                    }
                }
                showDuplicateMemberInfo(duplicateMember);
                memberRepository.save(duplicateMember);
                return duplicateMember;
            }
            log.info("kakaoId 중복은 회원가입 실패");
            return null;
        }
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

    @Transactional
    public Member registerNaverUserIfNeeded(NaverMemberInfoDto naverMemberInfo) {
        // 검증해야하는 email, username, id를 전부다
        log.info("registerNaverMemberIfNeeded() 검증 시작");
        String naverId = naverMemberInfo.getId();

        log.info("NaverMemberInfo.getId() : "+naverMemberInfo.getId());
        log.info("NaverMemberInfo.getEmail() : "+naverMemberInfo.getEmail());
        log.info("NaverMemberInfo.getNickname() : "+naverMemberInfo.getNickname());

        Member duplicateMember = memberRepository
                .findByNaverIdOrEmailOrUsername(
                        naverMemberInfo.getId(),
                        naverMemberInfo.getEmail(),
                        naverMemberInfo.getNickname()
                ).orElse(null);

        if(duplicateMember == null){
            log.info("중복되는 naverId, username, email없을때 새로운 계정 회원 가입");
            String uuid = UUID.randomUUID().toString();
            Member member = Member.builder()
                    .username(naverMemberInfo.getNickname())
                    .password(uuid)
                    .email(naverMemberInfo.getEmail())
                    .role(MemberRoleEnum.USER)
                    .naverId(naverMemberInfo.getId())
                    .build();

            memberRepository.save(member);
            return member;
        }else{
            log.info("username, email, naverId 셋 중 하나 이상이 중복될 때");
            log.info("naverMemberInfo.getId() : "+duplicateMember.getId());
            log.info("naverMemberInfo.getEmail() : "+duplicateMember.getEmail());
            log.info("naverMemberInfo.getNickname() : "+duplicateMember.getUsername());

            Member naverIdMember = memberRepository.findByNaverId(naverMemberInfo.getId()).orElse(null);

            // kakaoId를 가진 멤버가 없으면
            if(naverIdMember == null){
                log.info("kakaoId가 가진 멤버가 아닌 username, email 둘 중 하나가 중복될 때");
                log.info("구글 아이디, 네이버 아이디 탐색하기");
                Long kakaoId = duplicateMember.getKakaoId();
                String googleId = duplicateMember.getGoogleId();

                if (kakaoId != null) {
                    // kakaoId가 존재하는 멤버
                    if (googleId != null) {
                        // 둘다 가지는 멤버
                        log.info("kakaoId와 googleId를 둘 다 가지는 멤버");
                        log.info("kakaoId : "+kakaoId);
                        log.info("naverId : "+duplicateMember.getNaverId());
                        log.info("googleId : "+googleId);
                        duplicateMember.OauthIdUpdate(kakaoId, naverId, googleId);
                    } else {
                        // kakaoId만 가지는 멤버
                        log.info("kakaoId만 가지는 멤버");
                        log.info("kakaoId : "+kakaoId);
                        log.info("naverId : "+duplicateMember.getNaverId());
                        duplicateMember.OauthIdUpdate(kakaoId, naverId, null);
                        log.info("naverId : "+duplicateMember.getNaverId());
                    }
                } else {
                    // kakaoId를 가지지 않는 멤버
                    if (googleId == null) {
                        log.info("둘 다 가지지 않는 멤버");
                        duplicateMember.OauthIdUpdate(null, naverId, null);
                    } else {
                        log.info("googleId만 가지는 멤버");
                        log.info("naverId : "+duplicateMember.getKakaoId());
                        log.info("googleId : "+googleId);
                        duplicateMember.OauthIdUpdate(null, naverId, googleId);
                    }
                }
                showDuplicateMemberInfo(duplicateMember);
                memberRepository.save(duplicateMember);
                return duplicateMember;
            }
            log.info("naverId 중복은 회원가입 실패");
            return null;
        }
    }

    private GoogleMemberInfoDto getGoogleMemberInfo(String accessToken) throws JsonProcessingException {
        log.info("getGoogleMemberInfo() start! ");
        URI uri = UriComponentsBuilder
                // https:/www.googlepis.com/tokeninfo
                .fromUriString("https://www.googleapis.com/oauth2/v3/userinfo")
                .queryParam("access_token", accessToken).encode()
                .build()
                .toUri();
                // "Either access_token, id_token, or token_handle required"

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
        log.info("jsonNode.toString() : "+jsonNode.toPrettyString());

        log.info("jsonNode.get(id) : "+jsonNode.get("sub"));
        log.info("jsonNode.get(name) : "+jsonNode.get("name"));
        log.info("jsonNode.get(given_name) : "+jsonNode.get("given_name"));
        log.info("jsonNode.get(family_name) : "+jsonNode.get("family_name"));
        // 이메일은 존재하네?
        String id = jsonNode.get("sub").asText();
        // sub : 요청을 수행하는 주 구성원을 나타내는 ID입니다.
        String username = jsonNode.get("name").asText();

        String email = UUID.randomUUID().toString()+"@gmail.com";

        GoogleMemberInfoDto googleUserInfo = GoogleMemberInfoDto.builder()
                .id(id)
                .email(email)
                .username(username).build();
        return googleUserInfo;
    }

    @Transactional
    public Member registerGoogleMemberIfNeeded(GoogleMemberInfoDto googleMemberInfo) {
        // 검증해야하는 email, username, id를 전부다
        log.info("registerNaverMemberIfNeeded() 검증 시작");
        String googleId = googleMemberInfo.getId();

        log.info("googleMemberInfo.getId() : "+googleMemberInfo.getId());
        log.info("googleMemberInfo.getEmail() : "+googleMemberInfo.getEmail());
        log.info("googleMemberInfo.getNickname() : "+googleMemberInfo.getUsername());

        Member duplicateMember = memberRepository
                .findByGoogleIdOrEmailOrUsername(
                        googleMemberInfo.getId(),
                        googleMemberInfo.getEmail(),
                        googleMemberInfo.getUsername()
                ).orElse(null);

        if(duplicateMember == null){
            log.info("중복되는 naverId, username, email없을때 새로운 계정 회원 가입");
            String uuid = UUID.randomUUID().toString();
            Member member = Member.builder()
                    .username(googleMemberInfo.getUsername())
                    .password(uuid)
                    .email(googleMemberInfo.getEmail())
                    .role(MemberRoleEnum.USER)
                    .googleId(googleMemberInfo.getId())
                    .build();

            memberRepository.save(member);
            return member;
        }else{
            log.info("username, email, naverId 셋 중 하나 이상이 중복될 때");
            log.info("googleMemberInfo.getId() : "+duplicateMember.getId());
            log.info("googleMemberInfo.getEmail() : "+duplicateMember.getEmail());
            log.info("googleMemberInfo.getNickname() : "+duplicateMember.getUsername());

            Member googleIdMember = memberRepository.findByGoogleId(googleMemberInfo.getId()).orElse(null);

            // kakaoId를 가진 멤버가 없으면
            if(googleIdMember == null){
                log.info("kakaoId가 가진 멤버가 아닌 username, email 둘 중 하나가 중복될 때");
                log.info("구글 아이디, 네이버 아이디 탐색하기");
                Long kakaoId = duplicateMember.getKakaoId();
                String naverId = duplicateMember.getNaverId();

                if (kakaoId != null) {
                    // kakaoId가 존재하는 멤버
                    if (naverId != null) {
                        // 둘다 가지는 멤버
                        log.info("kakaoId와 naverId를 둘 다 가지는 멤버");
                        log.info("kakaoId : "+kakaoId);
                        log.info("naverId : "+naverId);
                        log.info("googleId : "+duplicateMember.getGoogleId());
                        duplicateMember.OauthIdUpdate(kakaoId, naverId, googleId);
                    } else {
                        // kakaoId만 가지는 멤버
                        log.info("kakaoId만 가지는 멤버");
                        log.info("kakaoId : "+kakaoId);
                        log.info("googleId : "+duplicateMember.getGoogleId());
                        duplicateMember.OauthIdUpdate(kakaoId, null, googleId);
                        log.info("googleId : "+duplicateMember.getGoogleId());
                    }
                } else {
                    // kakaoId를 가지지 않는 멤버
                    if (naverId == null) {
                        log.info("둘 다 가지지 않는 멤버");
                        duplicateMember.OauthIdUpdate(null, null, googleId);
                    } else {
                        log.info("naverId만 가지는 멤버");
                        log.info("naverId : "+naverId);
                        log.info("googleId : "+duplicateMember.getGoogleId());
                        duplicateMember.OauthIdUpdate(null, naverId, googleId);
                    }
                }
                showDuplicateMemberInfo(duplicateMember);
                memberRepository.save(duplicateMember);
                return duplicateMember;
            }
            log.info("googleId 중복은 회원가입 실패");
            return null;
        }
    }

    private void showDuplicateMemberInfo(Member duplicateMember){
        log.info("중복 되는 것이 존재 하는 멤버의 회원 가입 최종 정보");
        log.info("duplicateMember.getUsername() : "+duplicateMember.getUsername());
        log.info("duplicateMember.getEmail() : "+duplicateMember.getEmail());
        log.info("duplicateMember.getKakaoId() : "+duplicateMember.getKakaoId());
        log.info("duplicateMember.getNaverId() : "+duplicateMember.getNaverId());
        log.info("duplicateMember.getGoogleId() : "+duplicateMember.getGoogleId());
    }
}
