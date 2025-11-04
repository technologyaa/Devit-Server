package com.example.websocketchat.domain.member.dto;

import com.example.websocketchat.domain.member.entity.Member;

public record SignUpResponse(
        String username,
        String email,
        String createAt
) {
    public static SignUpResponse of(Member member) {
        return new SignUpResponse(
                member.getUsername(),
                member.getEmail(),
                member.getCreatedAt()
        );
    }
}

