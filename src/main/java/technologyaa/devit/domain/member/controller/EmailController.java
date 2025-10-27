package technologyaa.devit.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import technologyaa.devit.domain.member.dto.EmailRequest;
import technologyaa.devit.domain.member.service.EmailService;

import java.util.Map;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest requset){
        emailService.joinEmail(requset.email());
        return ResponseEntity.ok(Map.of("Message","이메일이 전송되었습니다."));
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkEmail(@RequestBody EmailRequest request) {
        return emailService.checkEmail(request.email(), request.authNum());
    }
}
