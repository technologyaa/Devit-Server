package technologyaa.devit.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import technologyaa.devit.domain.member.dto.SignInRequest;
import technologyaa.devit.domain.member.dto.SignUpRequest;
import technologyaa.devit.domain.member.entity.Member;
import technologyaa.devit.domain.member.entity.Role;
import technologyaa.devit.domain.member.repository.MemberRepository;
import technologyaa.devit.global.jwt.JwtProvider;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public ResponseEntity<?> signUp(SignUpRequest request) {
        try {
            Member member = Member.builder()
                    .username(request.username())
                    .password(passwordEncoder.encode(request.password()))
                    .email(request.email())
                    .phone(request.phone())
                    .age(request.age())
                    .role(Role.BASIC)
                    .build();
            memberRepository.save(member);
            return ResponseEntity.ok(Map.of("Message", "회원가입에 성공하셨습니다."));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    public ResponseEntity<?> signIn(SignInRequest request) {
        Member member = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        String token = jwtProvider.createToken(member.getUsername(), member.getRole());

        return ResponseEntity.ok(Map.of("token", token));
    }
}
