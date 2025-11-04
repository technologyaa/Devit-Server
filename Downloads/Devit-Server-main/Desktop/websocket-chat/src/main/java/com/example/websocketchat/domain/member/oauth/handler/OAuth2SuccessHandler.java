package com.example.websocketchat.domain.member.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.example.websocketchat.global.jwt.JwtProvider;
import com.example.websocketchat.domain.member.entity.Role;

import java.io.IOException;

/**
 * OAuth2 로그인 성공 시 처리하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        log.info("OAuth2 로그인 성공 - Email: {}, Name: {}", email, name);
        
        // OAuth 로그인 성공 시 JWT 토큰 생성 및 쿠키에 저장
        if (email != null) {
            String jwtToken = jwtProvider.createAccessToken(email, Role.ROLE_USER);
            
            Cookie cookie = new Cookie("accessToken", jwtToken);
            cookie.setPath("/");
            cookie.setHttpOnly(false); // JavaScript에서 접근 가능하도록 설정
            cookie.setMaxAge(3600); // 1시간
            response.addCookie(cookie);
            
            log.info("OAuth 로그인 후 JWT 토큰 생성 완료 - Email: {}", email);
        }
        
        // 기본 성공 URL로 리다이렉트
        // 로그인 성공 후 메인 페이지로 이동
        setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

