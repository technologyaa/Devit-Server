package com.example.websocketchat.domain.member.oauth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    /**
     * 메인 페이지
     * @param model 뷰에 전달할 데이터
     * @param oAuth2User OAuth2 인증된 사용자 정보 (없을 수 있음)
     * @return 뷰 이름
     */
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User != null) {
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");
            
            log.info("OAuth 사용자 접속 - Email: {}, Name: {}", email, name);
            
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("userEmail", email);
            model.addAttribute("userName", name);
            model.addAttribute("userPicture", picture);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        
        return "index";
    }

    /**
     * 로그인 페이지
     * @return 뷰 이름
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

