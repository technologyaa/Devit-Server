package com.example.websocketchat.domain.profile.controller;

import com.example.websocketchat.domain.profile.dto.ProfileResponse;
import com.example.websocketchat.domain.profile.service.ProfileService;
import com.example.websocketchat.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "프로필", description = "사용자 프로필 API (프론트엔드 사양)")
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "기본 프로필 조회", description = "로그인 사용자 대신 사용할 기본 프로필 정보를 제공합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile() {
        ProfileResponse response = profileService.getDefaultProfile();
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "프로필 정보를 조회했습니다.", response));
    }
}

