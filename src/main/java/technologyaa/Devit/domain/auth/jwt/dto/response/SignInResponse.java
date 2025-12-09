package technologyaa.Devit.domain.auth.jwt.dto.response;

public record SignInResponse(
        String accessToken,
        String refreshToken
) {
}

