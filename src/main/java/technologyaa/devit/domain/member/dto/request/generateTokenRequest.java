package technologyaa.devit.domain.member.dto.request;

import technologyaa.devit.domain.member.entity.Role;

public record generateTokenRequest(
        String username,
        Role role
) {
}
