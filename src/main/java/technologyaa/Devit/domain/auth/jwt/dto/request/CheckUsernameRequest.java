package technologyaa.Devit.domain.auth.jwt.dto.request;

import jakarta.validation.constraints.NotNull;

public record CheckUsernameRequest(
        @NotNull
        String username
) {
}

