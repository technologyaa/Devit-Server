package technologyaa.devit.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;

public record reGeneateTokenRequest(
        @NotNull
        String username,
        @NotNull
        String refreshToken
) {
}
