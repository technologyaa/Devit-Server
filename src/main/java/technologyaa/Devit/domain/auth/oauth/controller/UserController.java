package technologyaa.Devit.domain.auth.oauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import technologyaa.Devit.domain.auth.oauth.dto.UserInfoResponse;
import technologyaa.Devit.domain.auth.oauth.entity.User;
import technologyaa.Devit.domain.auth.oauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * OAuth 사용자 정보 관련 API 컨트롤러
 */
@Tag(name = "OAuth 사용자 (OAuth User)", description = "OAuth 인증 사용자 정보 API")
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자 정보 조회
     * 인증된 사용자의 프로필 정보를 반환합니다.
     * @param oAuth2User OAuth2 인증된 사용자 정보
     * @return 사용자 정보 응답
     */
    @Operation(summary = "현재 OAuth 사용자 조회", description = "현재 로그인한 OAuth 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.ok(UserInfoResponse.builder()
                    .isAuthenticated(false)
                    .build());
        }

        String email = oAuth2User.getAttribute("email");
        if (email == null || email.trim().isEmpty()) {
            log.warn("OAuth2User에서 이메일을 찾을 수 없음");
            return ResponseEntity.ok(UserInfoResponse.builder()
                    .isAuthenticated(false)
                    .build());
        }
        
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.warn("사용자 정보를 DB에서 찾을 수 없음 - Email: {}", email);
            return ResponseEntity.ok(UserInfoResponse.builder()
                    .email(email)
                    .name(oAuth2User.getAttribute("name"))
                    .picture(oAuth2User.getAttribute("picture"))
                    .isAuthenticated(true)
                    .build());
        }

        User user = userOptional.get();
        return ResponseEntity.ok(UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .picture(user.getPicture())
                .provider(user.getProvider())
                .isAuthenticated(true)
                .build());
    }
}

