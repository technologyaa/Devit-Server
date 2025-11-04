package com.example.websocketchat.domain.member.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 사용자 정보 응답 DTO
 */
@Schema(description = "사용자 정보 응답")
@Getter
@Builder
public class UserInfoResponse {
    @Schema(description = "사용자 ID", example = "1")
    private final Long id;
    
    @Schema(description = "이메일 주소", example = "user@example.com")
    private final String email;
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private final String name;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String picture;
    
    @Schema(description = "OAuth 제공자 (google, kakao 등)", example = "google")
    private final String provider;
    
    @Schema(description = "인증 여부", example = "true")
    private final boolean isAuthenticated;
}

