package com.example.websocketchat.domain.member.dto;

public record GenerateTokenRequest(
        String username,
        String role
) {
}

