package com.example.websocketchat.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.websocketchat.domain.member.dto.SignInRequest;
import com.example.websocketchat.domain.member.dto.SignUpRequest;
import com.example.websocketchat.domain.member.dto.SignUpResponse;
import com.example.websocketchat.domain.member.entity.Member;
import com.example.websocketchat.domain.member.repository.MemberRepository;
import com.example.websocketchat.global.dto.ApiResponse;
import com.example.websocketchat.global.exception.CustomException;
import com.example.websocketchat.domain.member.exception.AuthErrorCode;
import com.example.websocketchat.global.jwt.JwtProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public ApiResponse<?> signUp(SignUpRequest request) {
        if (memberRepository.existsByUsername(request.username())) {
            throw new CustomException(AuthErrorCode.User_Already_Exists);
        }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date = LocalDateTime.now().format(formatter);

            Member member = Member.builder()
                    .username(request.username())
                    .password(passwordEncoder.encode(request.password()))
                    .email(request.email())
                    .createdAt(date)
                    .credit(0L)
                    .role(request.role())
                    .build();
            memberRepository.save(member);
            return new ApiResponse<SignUpResponse>(true,200, "회원가입에 성공하셨습니다.", SignUpResponse.of(member));

        }


    public ResponseEntity<?> signIn(SignInRequest request) {
        Member member = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(AuthErrorCode.INVALID_PASSWORD);
        }
        String token = jwtProvider.createToken(member.getUsername(), member.getRole());

        ApiResponse<String> response = new ApiResponse<>(true,200, "로그인에 성공하셨습니다.", token);
        return ResponseEntity.ok(response);
    }
}

