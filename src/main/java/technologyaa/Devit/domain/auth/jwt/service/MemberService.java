package technologyaa.Devit.domain.auth.jwt.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.auth.jwt.dto.request.*;
import technologyaa.Devit.domain.auth.jwt.dto.response.SignInResponse;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.file.service.FileStorageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final TokenUseCase tokenUseCase;

    public APIResponse<String> signUp(SignUpRequest request) {
        if (memberRepository.existsByUsername(request.username())) {
            throw new AuthException(AuthErrorCode.MEMBER_ALREADY_EXISTS);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = LocalDateTime.now().format(formatter);

        Member member = Member.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .createdAt(date)
                .credit(0L)
                .role(request.role())
                .build();
        memberRepository.save(member);
        return APIResponse.ok("회원가입에 성공하셨습니다.");

    }


    public APIResponse<SignInResponse> signIn(SignInRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        GenerateTokenRequest generateTokenRequest = new GenerateTokenRequest(
                member.getUsername(),
                member.getRole()
        );

        String accessToken = tokenUseCase.generateAccessToken(generateTokenRequest, response);
        String refreshToken = tokenUseCase.generateRefreshToken(generateTokenRequest, response);

        return APIResponse.ok(new SignInResponse(accessToken,refreshToken));
    }

    public APIResponse<String> signOut(SignOutRequest request, HttpServletResponse response) {
        tokenUseCase.deleteToken(request, response);
        return APIResponse.ok("로그아웃 되었습니다.");
    }

    public APIResponse<?> uploadProfileImage(MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        if (member.getProfile() != null) {
            fileStorageService.deleteFile(member.getProfile());
        }

        try {
            String imagePath = fileStorageService.storeProfileFile(file);
            member.setProfile(imagePath);
            memberRepository.save(member);

            return APIResponse.ok(imagePath);

        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }
    }

    public boolean checkUsername(SignOutRequest request) {
        if(memberRepository.existsByUsername(request.username())) {
            throw new AuthException(AuthErrorCode.MEMBER_ALREADY_EXISTS);
        }
        return true;
    }

    public APIResponse<String> reGenerateAccessToken(ReGenerateTokenRequest request, HttpServletResponse response) {
        return tokenUseCase.reGenerateAccessToken(request, response);
    }
}

