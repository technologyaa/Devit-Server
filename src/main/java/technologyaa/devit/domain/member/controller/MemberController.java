package technologyaa.devit.domain.member.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import technologyaa.devit.domain.member.dto.request.SignInRequest;
import technologyaa.devit.domain.member.dto.request.SignOutRequest;
import technologyaa.devit.domain.member.dto.request.SignUpRequest;
import technologyaa.devit.domain.member.dto.request.reGeneateTokenRequest;
import technologyaa.devit.domain.member.dto.response.SignInResponse;
import technologyaa.devit.domain.member.service.MemberService;
import technologyaa.devit.domain.member.service.TokenUseCase;
import technologyaa.devit.global.data.ApiResponse;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final TokenUseCase tokenUseCase;

    @PostMapping("/signup")
    public ApiResponse<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        return memberService.signUp(signUpRequest);
    }

    @PostMapping("/signin")
    public ApiResponse<SignInResponse> signIn(@RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        return memberService.signIn(signInRequest, response);
    }

    @PostMapping("signout")
    public ApiResponse<String>  signOut(@RequestBody SignOutRequest signOutRequest) {
        return memberService.signOut(signOutRequest);
    }

    @PostMapping("/refresh")
    public ApiResponse<String> reGenerateAccessToken(@RequestBody reGeneateTokenRequest request, HttpServletResponse response) {
        return tokenUseCase.reGeneateAccessToken(request, response);
    }
}
