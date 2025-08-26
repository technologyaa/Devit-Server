package technologyaa.devit.domain.member.dto;

import jakarta.validation.constraints.NotNull;

public record SignUpRequest(
        @NotNull
        String username,
        @NotNull
        String password,
        @NotNull
        String email,
        @NotNull
        String phone,
        @NotNull
        int age
) {
}
