package technologyaa.Devit.domain.auth.jwt.dto.response;

import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.developer.entity.Developer;
import technologyaa.Devit.domain.developer.entity.Major;

import java.math.BigDecimal;

public record DeveloperResponse(
        Long id,
        String username,
        String email,
        String createdAt,
        Long credit,
        String role,
        boolean isDeveloper,
        String profile,
        Major major,
        String introduction,
        BigDecimal temperature
) {
    public static DeveloperResponse from(Member member, Developer developer) {
        return new DeveloperResponse(
                member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getCreatedAt(),
                member.getCredit(),
                member.getRole() != null ? member.getRole().toString() : null,
                member.isDeveloper(),
                member.getProfile(),
                developer != null ? developer.getMajor() : null,
                developer != null ? developer.getIntroduction() : null,
                developer != null ? developer.getTemperature() : null
        );
    }
}
