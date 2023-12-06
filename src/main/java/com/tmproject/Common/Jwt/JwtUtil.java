package com.tmproject.Common.Jwt;

import com.tmproject.api.member.entity.MemberRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";

    public static final String BEARER_PREFIX = "Bearer ";

    private static final long TOKEN_TIME = 60 * 60 * 1000L;
    // 1시간

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @Value("${jwt.secret_key}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init(){
        log.info("init start!");
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(String username, MemberRoleEnum role){
        log.info("토큰 생성");
        Date date = new Date();
        return BEARER_PREFIX + Jwts.builder()
                .setSubject(username)
                .claim(AUTHORIZATION_KEY,role)
                .setExpiration(new Date(date.getTime()+TOKEN_TIME))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String getJwtFromHeader(HttpServletRequest req){
        log.info("getJwtFromHeader()");
        String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            log.info("validateToken");
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    public Claims getUserInfoFromToken(String token) {
        log.info("getUserInfoFromToken");
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    // JWT 사용자 정보를 가져오기
}
