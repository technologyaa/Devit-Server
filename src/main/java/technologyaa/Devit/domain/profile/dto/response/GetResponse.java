package technologyaa.Devit.domain.profile.dto.response;

import technologyaa.Devit.domain.auth.jwt.entity.Member;

public record GetResponse(
        String username,
        String email,
        Long completedProjectCount
) {
    public static GetResponse of(Member member, Long completedProjectCount) {
        return new GetResponse(
                member.getUsername(),
                member.getEmail(),
                completedProjectCount
        );
    }
}