package technologyaa.Devit.domain.auth.jwt.dto;

import jakarta.validation.constraints.NotNull;

public record ReGenerateTokenRequest(
        @NotNull
        String username,
        @NotNull
        String refreshToken
) {
}

