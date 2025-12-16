package technologyaa.Devit.domain.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.profile.dto.request.UpdateRequest;
import technologyaa.Devit.domain.profile.service.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    /** 내 프로필 */
    @GetMapping
    public APIResponse<?> getProfile() {
        return profileService.getProfile();
    }

    /** 특정 유저 프로필 */
    @GetMapping("/{id}")
    public APIResponse<?> getProfileById(@PathVariable Long id) {
        return profileService.getProfileById(id);
    }

    /** 프로필 수정 */
    @PatchMapping
    public APIResponse<?> updateProfile(@RequestBody UpdateRequest request) {
        return profileService.updateProfile(request);
    }

    /** 프로필 삭제 */
    @DeleteMapping
    public APIResponse<?> deleteProfile() {
        return profileService.deleteProfile();
    }

    /** 기본 프로필 */
    @GetMapping("/default")
    public APIResponse<?> getDefaultProfile() {
        return profileService.getDefaultProfile();
    }
}