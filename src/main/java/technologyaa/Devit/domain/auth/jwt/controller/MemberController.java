package technologyaa.Devit.domain.auth.jwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.auth.jwt.dto.request.ReGenerateTokenRequest;
import technologyaa.Devit.domain.auth.jwt.dto.request.SignInRequest;
import technologyaa.Devit.domain.auth.jwt.dto.request.SignOutRequest;
import technologyaa.Devit.domain.auth.jwt.dto.request.SignUpRequest;
import technologyaa.Devit.domain.auth.jwt.service.MemberService;
import technologyaa.Devit.domain.common.APIResponse;


@Tag(name = "인증 (Auth)", description = "회원가입 및 로그인 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public APIResponse<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return memberService.signUp(signUpRequest);
    }

    @Operation(summary = "로그인", description = "사용자 로그인을 처리하고 JWT 토큰을 반환합니다.")
    @PostMapping("/signin")
    public APIResponse<?> signIn(@Valid @RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        return memberService.signIn(signInRequest, response);
    }

    @Operation(summary = "프로필 사진 변경", description = "사용자의 기존 프로필 사진을 삭제하고 새 프로필 사진을 저장합니다.")
    @PutMapping("/profile/image")
    public APIResponse<?> uploadProfileImage(@RequestParam("file")MultipartFile file) {
        return memberService.uploadProfileImage(file);
    }

    @Operation(summary = "아이디 중복 확인", description = "아이디가 기존에 있는 아이디인지 확인합니다.")
    @PostMapping("/check")
    public boolean checkandUsername(@Valid @RequestBody SignOutRequest request) {
        return memberService.checkUsername(request);
    }

    @Operation(summary = "로그아웃", description = "현재 로그인되어 있는 계정을 로그아웃합니다.(redis값을 삭제함)")
    @DeleteMapping("/signout")
    public APIResponse<?> signout(@Valid @RequestBody SignOutRequest request, HttpServletResponse response) {
        return memberService.signOut(request, response);
    }

    @Operation(summary = "재발급", description = "refresh token을 통해 access token을 재발급받는다.")
    @PostMapping("/refresh")
    public APIResponse<?> refresh(@Valid @RequestBody ReGenerateTokenRequest request, HttpServletResponse response) {
        return memberService.reGenerateAccessToken(request, response);
    }

    @Operation(summary = "현재 유저 조회", description = "현재 로그인한 유저의 정보를 조회합니다.")
    @GetMapping("/me")
    public APIResponse<?> getCurrentMember() {
        return memberService.getCurrentMember();
    }

    @Operation(summary = "모든 유저 조회", description = "모든 유저의 목록을 조회합니다.")
    @GetMapping("/members")
    public APIResponse<?> getAllMembers() {
        return memberService.getAllMembers();
    }
}

