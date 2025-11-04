package com.example.websocketchat.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "로그인 요청")
public record SignInRequest(
        @Schema(description = "사용자 이름", example = "홍길동", required = true)
        @NotNull
        String username,
        
        @Schema(description = "비밀번호", example = "password123", required = true)
        @NotNull
        String password
) {
}

