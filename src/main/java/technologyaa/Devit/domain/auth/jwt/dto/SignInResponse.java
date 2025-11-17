package technologyaa.Devit.domain.auth.jwt.dto;

public record SignInResponse(
        String accessToken,
        String refreshToken,
        String username
) {
}

