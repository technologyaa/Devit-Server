package technologyaa.Devit.domain.auth.jwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import technologyaa.Devit.domain.auth.jwt.dto.EmailRequest;
import technologyaa.Devit.domain.auth.jwt.service.EmailService;
import technologyaa.Devit.domain.common.APIResponse;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public APIResponse<?> sendEmail(@RequestBody EmailRequest requset){
        emailService.joinEmail(requset.email());
        return APIResponse.ok("이메일이 전송되었습니다.");
    }

    @PostMapping("/check")
    public APIResponse<?> checkEmail(@RequestBody EmailRequest request) {
        return emailService.checkEmail(request.email(), request.authNum());
    }
}