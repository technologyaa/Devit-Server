package technologyaa.Devit.domain.auth.jwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import technologyaa.Devit.domain.auth.jwt.dto.request.EmailRequest;
import technologyaa.Devit.domain.auth.jwt.service.EmailService;
import technologyaa.Devit.domain.common.APIResponse;

@Tag(name = "이메일 (Email)", description = "이메일 인증 API")
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "이메일 인증 코드 전송", description = "인증 코드를 이메일로 전송합니다.")
    @PostMapping("/send")
    public APIResponse<?> sendEmail(@RequestBody EmailRequest requset){
        emailService.joinEmail(requset.email());
        return APIResponse.ok("이메일이 전송되었습니다.");
    }

    @Operation(summary = "이메일 인증 코드 확인", description = "이메일 인증 코드가 올바른지 확인합니다.")
    @PostMapping("/check")
    public APIResponse<?> checkEmail(@RequestBody EmailRequest request) {
        return emailService.checkEmail(request.email(), request.authNum());
    }
}