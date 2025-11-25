package technologyaa.Devit.domain.auth.jwt.dto;

import jakarta.validation.constraints.NotNull;

public record SignOutRequest(
        @NotNull
        String username
) {
}

