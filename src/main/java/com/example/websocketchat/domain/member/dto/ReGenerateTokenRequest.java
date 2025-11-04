package com.example.websocketchat.domain.member.dto;

import jakarta.validation.constraints.NotNull;

public record ReGenerateTokenRequest(
        @NotNull
        String username,
        @NotNull
        String refreshToken
) {
}

