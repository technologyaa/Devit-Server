package technologyaa.Devit.domain.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import technologyaa.Devit.domain.profile.dto.request.UpdateRequest;
import technologyaa.Devit.domain.profile.dto.response.GetResponse;
import technologyaa.Devit.domain.profile.dto.ProfileResponse;
import technologyaa.Devit.domain.profile.service.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<GetResponse> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetResponse> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }

    @PatchMapping
    public ResponseEntity<String> updateProfile(@RequestBody UpdateRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfile() {
        profileService.deleteProfile();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/default")
    public ResponseEntity<ProfileResponse> getDefaultProfile() {
        return ResponseEntity.ok(profileService.getDefaultProfile());
    }
}