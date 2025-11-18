package technologyaa.Devit.domain.auth.jwt.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.jwt.dto.GenerateTokenRequest;
import technologyaa.Devit.domain.auth.jwt.dto.ReGenerateTokenRequest;
import technologyaa.Devit.domain.auth.jwt.dto.SignOutRequest;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.global.config.RedisConfig;
import technologyaa.Devit.global.jwt.JwtProvider;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenUseCase {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RedisConfig redisConfig;

    public String generateAccessToken(GenerateTokenRequest request, HttpServletResponse response) {
        String accessToken = jwtProvider.createAccessToken(request.username(), request.role());
        redisConfig.redisTemplate().opsForValue().set("accessToken:" + request.username(), accessToken, 1, TimeUnit.DAYS);

        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(86400);
        response.addCookie(cookie);

        return accessToken;
    }

    public String generateRefreshToken(GenerateTokenRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.createRefreshToken(request.username());
        redisConfig.redisTemplate().opsForValue().set("refreshToken:" + request.username(), refreshToken, 7, TimeUnit.DAYS);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(604800);
        response.addCookie(cookie);

        return refreshToken;
    }

    public boolean isTokenValid(ReGenerateTokenRequest request) {
        ValueOperations<String, String> valueOperations = redisConfig.redisTemplate().opsForValue();
        String clientRefreshToken = request.refreshToken();
        String savedRefreshToken = valueOperations.get("refreshToken:" + request.username());
        if (savedRefreshToken != null && !savedRefreshToken.isEmpty()) {
            return clientRefreshToken.equals(savedRefreshToken);
        }
        return false;
    }

    public APIResponse<String> reGenerateAccessToken(ReGenerateTokenRequest request, HttpServletResponse response) {
        if (isTokenValid(request)) {
            Member member = memberRepository.findByUsername(request.username())
                    .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
            GenerateTokenRequest generateTokenRequest = new GenerateTokenRequest(
                    member.getUsername(),
                    member.getRole()
            );
            String newAccessToken = generateAccessToken(generateTokenRequest, response);
            return APIResponse.ok(newAccessToken);
        }
        throw new AuthException(AuthErrorCode.INVALID_TOKEN);
    }

    public void deleteToken(SignOutRequest request) {
        ValueOperations<String, String> valueOperations = redisConfig.redisTemplate().opsForValue();
        String savedAccessToken = valueOperations.get("accessToken:" + request.username());
        String savedRefreshToken = valueOperations.get("refreshToken:" + request.username());
        if (savedRefreshToken == null || savedAccessToken == null) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
        redisConfig.redisTemplate().delete("accessToken:" + request.username());
        redisConfig.redisTemplate().delete("refreshToken:" + request.username());
    }
}

