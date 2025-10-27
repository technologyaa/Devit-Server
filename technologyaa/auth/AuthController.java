//package com.demo.technologyaa.auth;
//import org.springframework.security.core.AuthenticationException;
//import com.demo.technologyaa.auth.dto.AuthResponse;
//import com.demo.technologyaa.auth.dto.LoginRequest;
//import com.demo.technologyaa.auth.dto.SignupRequest;
//import com.demo.technologyaa.user.User;
//import com.demo.technologyaa.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.*;
//import org.springframework.security.authentication.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationManager authenticationManager;
//
//    @PostMapping("/signup")
//    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
//        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 이메일입니다.");
//        }
//
//        User user = User.builder()
//                .email(req.getEmail())
//                .password(passwordEncoder.encode(req.getPassword()))
//                .nickname(req.getNickname())
//                .build();
//
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getEmail(), user.getNickname(), "회원가입 성공"));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request) {
//        UsernamePasswordAuthenticationToken token =
//                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());
//
//        try {
//            Authentication auth = authenticationManager.authenticate(token);
//
//            // SecurityContext에 인증 정보 저장
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
//            // 세션 생성 (JSESSIONID 쿠키가 클라이언트에 설정됨)
//            request.getSession(true);
//
//            User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
//
//            return ResponseEntity.ok(new AuthResponse(user.getId(), user.getEmail(), user.getNickname(), "로그인 성공"));
//        } catch (AuthenticationException ex) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 잘못된 이메일 또는 비밀번호");
//        }
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session != null) session.invalidate();
//        SecurityContextHolder.clearContext();
//        return ResponseEntity.ok("로그아웃 완료");
//    }
//}


package com.demo.technologyaa.auth;

import com.demo.technologyaa.auth.dto.AuthResponse;
import com.demo.technologyaa.auth.dto.LoginRequest;
import com.demo.technologyaa.auth.dto.SignupRequest;
import com.demo.technologyaa.user.User;
import com.demo.technologyaa.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .nickname(req.getNickname())
                .build();

        userRepository.save(user);

        return ResponseEntity.ok(new AuthResponse(
                user.getId(), user.getEmail(), user.getNickname(), "회원가입 성공"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());

        try {
            Authentication auth = authenticationManager.authenticate(token);

            SecurityContextHolder.getContext().setAuthentication(auth);

            // 세션 생성
            request.getSession(true);

            User user = userRepository.findByEmail(req.getEmail())
                    .orElseThrow(() -> new NoSuchElementException("사용자 없음"));

            return ResponseEntity.ok(new AuthResponse(
                    user.getId(), user.getEmail(), user.getNickname(), "로그인 성공"
            ));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 실패: 잘못된 이메일 또는 비밀번호");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("로그아웃 완료");
    }
}
