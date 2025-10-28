package technologyaa.devit.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import technologyaa.devit.domain.member.dto.request.SignInRequest;
import technologyaa.devit.domain.member.dto.request.SignOutRequest;
import technologyaa.devit.domain.member.dto.request.SignUpRequest;
import technologyaa.devit.domain.member.dto.request.generateTokenRequest;
import technologyaa.devit.domain.member.dto.response.SignInResponse;
import technologyaa.devit.domain.member.entity.Member;
import technologyaa.devit.domain.member.repository.MemberRepository;
import technologyaa.devit.global.data.ApiResponse;
import technologyaa.devit.global.exception.CustomException;
import technologyaa.devit.domain.member.exception.AuthErrorCode;
import technologyaa.devit.global.jwt.JwtProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenUseCase tokenUseCase;

    public ApiResponse<?> signUp(SignUpRequest request) {
        if (memberRepository.existsByUsername(request.username())) {
            throw new CustomException(AuthErrorCode.User_Already_Exists);
        }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date = LocalDateTime.now().format(formatter);

            Member member = Member.builder()
                    .username(request.username())
                    .password(passwordEncoder.encode(request.password()))
                    .email(request.email())
                    .createdAt(date)
                    .role(request.role())
                    .build();
            memberRepository.save(member);
            return ApiResponse.ok("회원가입에 성공했습니다.");

        }
        public ApiResponse<SignInResponse> signIn(SignInRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        if (passwordEncoder.matches(member.getPassword(), request.password())) {
            throw new CustomException(AuthErrorCode.INVALID_PASSWORD);
        }
            generateTokenRequest generateTokenRequest = new generateTokenRequest(
                    member.getUsername(),
                    member.getRole()
            );
        String accessToken = tokenUseCase.generateAccessToken(generateTokenRequest, response);
        tokenUseCase.generateRefreshToken(generateTokenRequest, response);
        return ApiResponse.ok(new SignInResponse(accessToken));
    }

    public ApiResponse<String> signOut(SignOutRequest request) {
        tokenUseCase.deleteToken(request);
        return ApiResponse.ok("로그아웃이 정상적으로 처리되었습니다.");
    }
}
