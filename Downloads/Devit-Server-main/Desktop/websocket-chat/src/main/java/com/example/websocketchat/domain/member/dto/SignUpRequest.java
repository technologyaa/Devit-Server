package com.example.websocketchat.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import com.example.websocketchat.domain.member.entity.Role;

@Schema(description = "회원가입 요청")
public record SignUpRequest(
        @Schema(description = "사용자 이름", example = "홍길동", required = true)
        @NotNull
        String username,
        
        @Schema(description = "비밀번호", example = "password123", required = true)
        @NotNull
        String password,
        
        @Schema(description = "이메일 주소", example = "user@example.com", required = true)
        @NotNull
        String email,
        
        @Schema(description = "사용자 권한", example = "ROLE_USER")
        Role role
)
{ }

