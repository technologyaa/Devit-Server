package com.example.websocketchat.domain.profile.controller;

import com.example.websocketchat.domain.profile.dto.request.Updaterequest;
import com.example.websocketchat.domain.profile.dto.response.GetProfileRes;
import com.example.websocketchat.domain.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile") // 반드시 /api/profile로 통일
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public GetProfileRes getMyProfile() {
        return profileService.getProfile();
    }

    @PutMapping
    public String updateProfile(@RequestBody Updaterequest request) {
        return profileService.updateProfile(request);
    }

    @DeleteMapping
    public void deleteProfile() {
        profileService.deleteProfile();
    }

    @GetMapping("/{id}")
    public GetProfileRes getProfileById(@PathVariable Long id) {
        return profileService.getProfileById(id);
    }
}