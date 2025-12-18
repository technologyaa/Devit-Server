package technologyaa.Devit.domain.profile.dto.response;

import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.entity.Role;

public record ProfileResponse(
        String username,
        String email,
        Long credit,
        Role role,
        String profile,
        Long completedProjectCount
) {
        public static ProfileResponse of(Member member, Long completedProjectCount) {
                return new ProfileResponse(
                        member.getUsername(),
                        member.getEmail(),
                        member.getCredit(),
                        member.getRole(),
                        member.getProfile(),
                        completedProjectCount
                );
        }
}