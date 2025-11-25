package technologyaa.Devit.domain.auth.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.jwt.dto.SignInRequest;
import technologyaa.Devit.domain.auth.jwt.dto.SignUpRequest;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.global.jwt.JwtProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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


    public APIResponse<?> signIn(SignInRequest request) {
        Member member = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }
        String token = jwtProvider.createAccessToken(member.getUsername(), member.getRole());

        return APIResponse.ok(token);
    }
}

