package com.example.websocketchat.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.websocketchat.domain.member.entity.Role;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    private final SecretKey secretKey;
    // Access Token 유효기간: 1시간 (3600000ms)
    public static final long ACCESS_TOKEN_VALIDITY = 3600000;
    // Refresh Token 유효기간: 7일 (604800000ms)
    public static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 3600 * 1000;
    private final long validityInSeconds = ACCESS_TOKEN_VALIDITY;

    public JwtProvider() {
        // TODO: 운영 환경에서는 환경변수(JWT_SECRET)를 사용하도록 수정 필요
        // 임시 개발용 시크릿 키 (application.properties의 Jwt.secret 값과 동일하게 유지)
        String defaultSecret = "mySecretKeyForDevelopmentOnlyPleaseChangeInProduction";
        this.secretKey = Keys.hmacShaKeyFor(defaultSecret.getBytes());
    }


    public String createToken(String username, Role role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityInSeconds);
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String createAccessToken(String username, Role role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityInSeconds);
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String username) {
        Date now = new Date();
        // Refresh Token은 7일 유효
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public Role getRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String roleString = claims.get("role", String.class);
        return Role.valueOf(roleString);
    }

    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                log.warn("토큰이 비어있습니다.");
                return false;
            }
            
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (JwtException e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

