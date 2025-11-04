package com.example.websocketchat.domain.member.oauth.controller;

import com.example.websocketchat.domain.member.entity.Member;
import com.example.websocketchat.domain.member.oauth.dto.UserInfoResponse;
import com.example.websocketchat.domain.member.oauth.entity.User;
import com.example.websocketchat.domain.member.oauth.repository.UserRepository;
import com.example.websocketchat.domain.member.repository.MemberRepository;
import com.example.websocketchat.global.jwt.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 사용자 정보 관련 API 컨트롤러 (OAuth 및 JWT 모두 지원)
 */
@Tag(name = "사용자 (User)", description = "사용자 정보 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    /**
     * 현재 로그인한 사용자 정보 조회
     * OAuth 또는 JWT 인증된 사용자의 프로필 정보를 반환합니다.
     * @param authentication 인증 정보 (OAuth 또는 JWT)
     * @return 사용자 정보 응답
     */
    @Operation(
            summary = "현재 사용자 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다. OAuth 또는 JWT 인증 모두 지원합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoResponse.class))
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(Authentication authentication) {
        log.info("GET /api/users/me - authentication: {}", authentication != null ? authentication.getClass().getSimpleName() : "null");
        
        // 인증되지 않은 경우
        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("인증되지 않은 사용자");
            return ResponseEntity.ok(UserInfoResponse.builder()
                    .isAuthenticated(false)
                    .build());
        }
        
        log.info("인증된 사용자 - Principal 타입: {}", authentication.getPrincipal().getClass().getSimpleName());

        // OAuth 사용자 확인
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
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
        
        // JWT 사용자 확인 (MemberDetails 또는 UserDetails)
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            
            // MemberDetails인 경우
            if (userDetails instanceof MemberDetails) {
                MemberDetails memberDetails = (MemberDetails) userDetails;
                Member member = memberDetails.getMember();
                return ResponseEntity.ok(UserInfoResponse.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .name(member.getUsername())
                        .isAuthenticated(true)
                        .build());
            }
            
            // OAuthUserDetails인 경우 (MemberDetailsService의 내부 클래스)
            // 이메일로 User 테이블에서 찾기
            Optional<User> userOptional = userRepository.findByEmail(username);
            if (userOptional.isPresent()) {
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
            
            // Member 테이블에서 찾기
            Optional<Member> memberOptional = memberRepository.findByUsername(username);
            if (memberOptional.isPresent()) {
                Member member = memberOptional.get();
                return ResponseEntity.ok(UserInfoResponse.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .name(member.getUsername())
                        .isAuthenticated(true)
                        .build());
            }
        }

        return ResponseEntity.ok(UserInfoResponse.builder()
                .isAuthenticated(false)
                .build());
    }
}

