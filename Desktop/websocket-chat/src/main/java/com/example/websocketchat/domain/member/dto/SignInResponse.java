package com.example.websocketchat.domain.member.dto;

public record SignInResponse(
        String accessToken,
        String refreshToken,
        String username
) {
}

