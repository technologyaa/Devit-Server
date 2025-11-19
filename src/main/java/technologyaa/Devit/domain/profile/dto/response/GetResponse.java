package technologyaa.Devit.domain.profile.dto.response;

import technologyaa.Devit.domain.auth.jwt.entity.Member;

public record GetResponse(
        String username,
        String email,
        String createdAt,
        Long credit,
        String role
) {
    public static GetResponse of(Member member) {
        return new GetResponse(
                member.getUsername(),
                member.getEmail(),
                member.getCreatedAt(),
                member.getCredit(),
                member.getRole() != null ? member.getRole().name() : "ROLE_USER"
        );
    }
}