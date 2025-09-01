package com.demo.technologyaa.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(new UserDto(u.getId(), u.getEmail(), u.getNickname())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new UserDto(null, null, "사용자 없음")));
    }

}

