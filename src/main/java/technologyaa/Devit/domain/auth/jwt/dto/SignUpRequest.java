package technologyaa.Devit.domain.auth.jwt.dto;

import jakarta.validation.constraints.NotNull;
import technologyaa.Devit.domain.auth.jwt.entity.Role;

public record SignUpRequest(
        @NotNull
        String username,
        @NotNull
        String password,
        @NotNull
        String email,
        Role role
)
{ }

