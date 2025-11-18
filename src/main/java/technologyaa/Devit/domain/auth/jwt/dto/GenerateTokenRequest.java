package technologyaa.Devit.domain.auth.jwt.dto;

import technologyaa.Devit.domain.auth.jwt.entity.Role;

public record GenerateTokenRequest(
        String username,
        Role role
) {
}

