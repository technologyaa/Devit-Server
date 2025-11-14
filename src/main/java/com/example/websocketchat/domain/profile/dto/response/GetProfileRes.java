package com.example.websocketchat.domain.profile.dto.response;

import com.example.websocketchat.domain.member.entity.Member;

public record GetProfileRes(
        String username,
        String email,
        String createdAt,
        Long credit,
        String role
) {
    public static GetProfileRes of(Member member) {
        String roleString = member.getRole() != null ? member.getRole().name() : "의뢰인";
        return new GetProfileRes(
                member.getUsername(),
                member.getEmail(),
                member.getCreatedAt(),
                member.getCredit(),
                roleString
        );
    }
}
