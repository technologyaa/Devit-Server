package technologyaa.Devit.domain.auth.jwt.dto.response;


import technologyaa.Devit.domain.auth.jwt.entity.Member;

public record SignUpResponse(
        String username,
        String email,
        String createAt
) {
    public static SignUpResponse of(Member member) {
        return new SignUpResponse(
                member.getUsername(),
                member.getEmail(),
                member.getCreatedAt()
        );
    }
}

