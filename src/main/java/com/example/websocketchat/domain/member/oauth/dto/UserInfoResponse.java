package com.example.websocketchat.domain.member.oauth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 사용자 정보 응답 DTO
 */
@Getter
@Builder
public class UserInfoResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String picture;
    private final String provider;
    private final boolean isAuthenticated;
}

