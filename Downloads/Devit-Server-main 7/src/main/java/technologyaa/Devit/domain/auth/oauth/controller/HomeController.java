package technologyaa.Devit.domain.auth.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * 로그인 페이지
     * @return 뷰 이름
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

