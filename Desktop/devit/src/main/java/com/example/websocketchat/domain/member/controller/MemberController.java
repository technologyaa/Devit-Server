package com.example.websocketchat.domain.member.controller;

import com.example.websocketchat.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.websocketchat.domain.member.dto.SignInRequest;
import com.example.websocketchat.domain.member.dto.SignUpRequest;
import com.example.websocketchat.domain.member.service.MemberService;


@Tag(name = "인증 (Auth)", description = "회원가입 및 로그인 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;

    @Operation(summary = "테스트", description = "MemberController 동작 확인용 API")
    @GetMapping("/test")
    public String test() {
        return "MemberController is working!";
    }

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ApiResponse<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        return memberService.signUp(signUpRequest);
    }

    @Operation(summary = "로그인", description = "사용자 로그인을 처리하고 JWT 토큰을 반환합니다.")
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInRequest signInRequest) {
        return memberService.signIn(signInRequest);
    }
}

