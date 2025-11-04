package com.example.websocketchat.domain.member.dto;

public record EmailRequest(
        String email,
        String authNum
) {
}

