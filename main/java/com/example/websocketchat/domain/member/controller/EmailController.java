package com.example.websocketchat.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.websocketchat.domain.member.dto.EmailRequest;
import com.example.websocketchat.domain.member.service.EmailService;
import com.example.websocketchat.global.dto.ApiResponse;

import java.util.Map;

// @RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ApiResponse<?> sendEmail(@RequestBody EmailRequest requset){
        emailService.joinEmail(requset.email());
        return new ApiResponse<>(true, 200, "이메일 전송되었습니다.", null);
    }

    @PostMapping("/check")
    public ApiResponse<?> checkEmail(@RequestBody EmailRequest request) {
        return emailService.checkEmail(request.email(), request.authNum());
    }
}

