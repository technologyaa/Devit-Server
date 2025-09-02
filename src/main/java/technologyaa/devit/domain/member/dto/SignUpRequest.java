package technologyaa.devit.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import technologyaa.devit.domain.member.entity.Role;

public record SignUpRequest(
        @NotNull
        String username,
        @NotNull
        String password,
        @NotNull
        String email,
        @NotNull
        String phone
) {
}
