/* 
package com.example.websocketchat.domain.member.service;

import com.example.websocketchat.domain.member.dto.GenerateTokenRequest;
import com.example.websocketchat.domain.member.dto.ReGenerateTokenRequest;
import com.example.websocketchat.domain.member.dto.SignOutRequest;
import com.example.websocketchat.domain.member.entity.Member;
import com.example.websocketchat.domain.member.exception.AuthErrorCode;
import com.example.websocketchat.domain.member.repository.MemberRepository;
import com.example.websocketchat.global.config.RedisConfig;
import com.example.websocketchat.global.dto.ApiResponse;
import com.example.websocketchat.global.exception.CustomException;
import com.example.websocketchat.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

// Redis를 사용하지 않을 경우 임시 비활성화
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenUseCase {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    // private final RedisConfig redisConfig;

    public String generateAccessToken(GenerateTokenRequest request, HttpServletResponse response) {
        String accessToken = jwtProvider.createAccessToken(request.username(), request.role());
        // redisConfig.redisTemplate().opsForValue().set("accessToken:" + request.username(), accessToken, 1, TimeUnit.DAYS);

        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(86400);
        response.addCookie(cookie);

        return accessToken;
    }

    public String generateRefreshToken(GenerateTokenRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.createRefreshToken(request.username());
        // redisConfig.redisTemplate().opsForValue().set("refreshToken:" + request.username(), refreshToken, 7, TimeUnit.DAYS);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(604800);
        response.addCookie(cookie);

        return refreshToken;
    }

    public boolean isTokenValid(ReGenerateTokenRequest request) {
        // ValueOperations<String, String> valueOperations = redisConfig.redisTemplate().opsForValue();
        String clientRefreshToken = request.refreshToken();
        // String savedRefreshToken = valueOperations.get("refreshToken:" + request.username());
        // if (savedRefreshToken != null && !savedRefreshToken.isEmpty()) {
        //     return clientRefreshToken.equals(savedRefreshToken);
        // }
        return false;
    }

    public ApiResponse<String> reGenerateAccessToken(ReGenerateTokenRequest request, HttpServletResponse response) {
        if (isTokenValid(request)) {
            Member member = memberRepository.findByUsername(request.username())
                    .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
            GenerateTokenRequest generateTokenRequest = new GenerateTokenRequest(
                    member.getUsername(),
                    member.getRole()
            );
            String newAccessToken = generateAccessToken(generateTokenRequest, response);
            return ApiResponse.ok(newAccessToken);
        }
        throw new CustomException(AuthErrorCode.INVALID_JWT);
    }

    public void deleteToken(SignOutRequest request) {
        // ValueOperations<String, String> valueOperations = redisConfig.redisTemplate().opsForValue();
        // String savedAccessToken = valueOperations.get("accessToken:" + request.username());
        // String savedRefreshToken = valueOperations.get("refreshToken:" + request.username());
        // if (savedRefreshToken == null || savedAccessToken == null) {
        //     throw new CustomException(AuthErrorCode.INVALID_JWT);
        // }
        // redisConfig.redisTemplate().delete("accessToken:" + request.username());
        // redisConfig.redisTemplate().delete("refreshToken:" + request.username());
    }
}
*/

