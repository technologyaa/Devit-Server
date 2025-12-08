package technologyaa.Devit.domain.auth.jwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.auth.jwt.dto.SignInRequest;
import technologyaa.Devit.domain.auth.jwt.dto.SignOutRequest;
import technologyaa.Devit.domain.auth.jwt.dto.SignUpRequest;
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
    public APIResponse<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        return memberService.signUp(signUpRequest);
    }

    @Operation(summary = "로그인", description = "사용자 로그인을 처리하고 JWT 토큰을 반환합니다.")
    @PostMapping("/signin")
    public APIResponse<?> signIn(@RequestBody SignInRequest signInRequest) {
        return memberService.signIn(signInRequest);
    }

    @Operation(summary = "프로필 사진 변경", description = "사용자의 기존 프로필 사진을 삭제하고 새 프로필 사진을 저장합니다.")
    @PutMapping("/profile/image")
    public APIResponse<?> uploadProfileImage(@RequestParam("file")MultipartFile file) {
        return memberService.uploadProfileImage(file);
    }

    @Operation(summary = "아이디 중복 확인", description = "아이디가 기존에 있는 아이디인지 확인합니다.")
    @PostMapping("/check")
    public boolean checkandUsername(@RequestBody SignOutRequest request) {
        return memberService.checkUsername(request);
    }
}

