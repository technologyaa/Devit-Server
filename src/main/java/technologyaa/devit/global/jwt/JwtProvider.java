package technologyaa.devit.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import technologyaa.devit.domain.member.entity.Role;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    private final SecretKey secretKey;
    @Value("${Jwt.access-token-expiration}")
    private int accessTokenExpiration;
    @Value("${Jwt.refresh-token-expiration}")
    private int refreshTokenExpiration;

    public JwtProvider(@Value("${Jwt.secret}")String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createAccessToken(String username, Role role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);
        return Jwts.builder()
                .subject("ACCESS")
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject("REFRESH")
                .claim("username", username)
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
        return claims.get("username", String.class);
    }

//    public Role getRole(String token) {
//        Claims claims = Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//        String roleString = claims.get("role", String.class);
//        return Role.valueOf(roleString);
//    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (JwtException e) {
            log.error(e.getMessage());
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
