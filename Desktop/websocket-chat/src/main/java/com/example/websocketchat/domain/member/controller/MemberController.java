package com.example.websocketchat.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.example.websocketchat.domain.member.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.websocketchat.domain.member.dto.SignInRequest;
import com.example.websocketchat.domain.member.dto.SignUpRequest;
import com.example.websocketchat.global.dto.ApiResponse;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;

    @GetMapping("/test")
    public String test() {
        return "MemberController is working!";
    }

    @PostMapping("/signup")
    public ApiResponse<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        return memberService.signUp(signUpRequest);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInRequest signInRequest) {
        return memberService.signIn(signInRequest);
    }
}

