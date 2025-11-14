package com.example.websocketchat.domain.profile.controller;

import com.example.websocketchat.domain.profile.dto.ProfileResponse;
import com.example.websocketchat.domain.profile.dto.request.Updaterequest;
import com.example.websocketchat.domain.profile.dto.response.GetProfileRes;
import com.example.websocketchat.domain.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    // 1. HEAD (당신 쪽) 기능: 로그인 사용자 프로필 조회 (GET /api/profile)
    @GetMapping
    public ResponseEntity<GetProfileRes> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    // 2. HEAD (당신 쪽) 기능: ID로 프로필 조회 (GET /api/profile/{id})
    @GetMapping("/{id}")
    public ResponseEntity<GetProfileRes> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }

    // 3. HEAD (당신 쪽) 기능: 프로필 수정 (PATCH /api/profile)
    @PatchMapping
    public ResponseEntity<String> updateProfile(@RequestBody Updaterequest request) {
        String updatedUsername = profileService.updateProfile(request);
        return ResponseEntity.ok(updatedUsername);
    }

    // 4. HEAD (당신 쪽) 기능: 프로필 삭제 (DELETE /api/profile)
    @DeleteMapping
    public ResponseEntity<Void> deleteProfile() {
        profileService.deleteProfile();
        return ResponseEntity.noContent().build();
    }

    // 5. REMOTE (친구 쪽) 기능: 기본 프로필 조회 (GET /api/profile/default)
    @GetMapping("/default")
    public ResponseEntity<ProfileResponse> getDefaultProfile() {
        ProfileResponse profile = profileService.getDefaultProfile();
        return ResponseEntity.ok(profile);
    }
}