package technologyaa.Devit.domain.auth.jwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import technologyaa.Devit.domain.auth.jwt.entity.Member;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String username;
    private String email;
    private String createdAt;
    private Long credit;
    private String role;
    private boolean isDeveloper;
    private String profile;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .credit(member.getCredit())
                .role(member.getRole() != null ? member.getRole().toString() : null)
                .isDeveloper(member.isDeveloper())
                .profile(member.getProfile())
                .build();
    }
}

