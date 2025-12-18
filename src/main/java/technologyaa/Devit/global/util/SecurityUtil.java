package technologyaa.Devit.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final MemberRepository memberRepository;

    public Member getMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        // anonymousUser 체크를 안전하게 처리
        Object principal = auth.getPrincipal();
        if (principal == null || "anonymousUser".equals(principal.toString())) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        String username = auth.getName();
        if (username == null || username.trim().isEmpty()) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
    }
}
