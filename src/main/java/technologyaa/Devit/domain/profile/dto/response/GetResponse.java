package technologyaa.Devit.domain.profile.dto.response;

import technologyaa.Devit.domain.auth.jwt.entity.Member;

public record GetResponse(
        String username,
        String email
) {
    public static GetResponse of(Member member) {
        return new GetResponse(
                member.getUsername(),
                member.getEmail()
        );
    }
}