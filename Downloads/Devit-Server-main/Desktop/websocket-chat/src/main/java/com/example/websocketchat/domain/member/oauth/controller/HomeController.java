package com.example.websocketchat.domain.member.oauth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    /**
     * 메인 페이지 - 정적 파일로 리다이렉트
     * @param oAuth2User OAuth2 인증된 사용자 정보 (없을 수 있음)
     * @return 리다이렉트 경로
     */
    @GetMapping("/")
    public String index(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User != null) {
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            log.info("OAuth 사용자 접속 - Email: {}, Name: {}", email, name);
        }
        
        // 정적 파일로 리다이렉트
        return "redirect:/index.html";
    }

    /**
     * 로그인 페이지 - 정적 파일로 리다이렉트
     * @return 리다이렉트 경로
     */
    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }
}

