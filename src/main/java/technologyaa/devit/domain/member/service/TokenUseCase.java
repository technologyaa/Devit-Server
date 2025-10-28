package technologyaa.devit.domain.member.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import technologyaa.devit.domain.member.dto.request.SignOutRequest;
import technologyaa.devit.domain.member.dto.request.generateTokenRequest;
import technologyaa.devit.domain.member.dto.request.reGeneateTokenRequest;
import technologyaa.devit.domain.member.entity.Member;
import technologyaa.devit.domain.member.exception.AuthErrorCode;
import technologyaa.devit.domain.member.repository.MemberRepository;
import technologyaa.devit.global.config.RedisConfig;
import technologyaa.devit.global.data.ApiResponse;
import technologyaa.devit.global.exception.CustomException;
import technologyaa.devit.global.jwt.JwtProvider;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenUseCase {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RedisConfig redisConfig;

    public String generateAccessToken(generateTokenRequest request, HttpServletResponse response) {
        String accessToken = jwtProvider.createAccessToken(request.username(), request.role());
        redisConfig.redisTemplate().opsForValue().set("accessToken:" + request.username(), accessToken, 1, TimeUnit.DAYS);

        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(86400);
        response.addCookie(cookie);

        return accessToken;
    }

    public String generateRefreshToken(generateTokenRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.createRefreshToken(request.username());
        redisConfig.redisTemplate().opsForValue().set("refreshToken:" + request.username(), refreshToken, 7, TimeUnit.DAYS);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(604800);
        response.addCookie(cookie);

        return refreshToken;
    }
    public boolean isTokenValid(reGeneateTokenRequest request) {
        ValueOperations<String, String> valueOperations = redisConfig.redisTemplate().opsForValue();
        String cailentRefreshToken = request.refreshToken();
        String savedRefreshToken = valueOperations.get("refreshToken:" + request.username());
        if (savedRefreshToken != null || !savedRefreshToken.isEmpty()) {
            return cailentRefreshToken.equals(savedRefreshToken);
        }
        return false;
    }

    public ApiResponse<String> reGeneateAccessToken(reGeneateTokenRequest request, HttpServletResponse response) {
        if (isTokenValid(request)) {
            Member member = memberRepository.findByUsername(request.username())
                    .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
            generateTokenRequest generateTokenRequest = new generateTokenRequest(
                    member.getUsername(),
                    member.getRole()
            );
            String newAccessToken = generateAccessToken(generateTokenRequest, response);
            return ApiResponse.ok(newAccessToken);
        }
        throw new CustomException(AuthErrorCode.INVALID_JWT);
    }

    public void deleteToken(SignOutRequest request) {
        ValueOperations<String, String> valueOperations = redisConfig.redisTemplate().opsForValue();
        String savedAccessToken = valueOperations.get("accessToken:" + request.username());
        String savedRefreshToken = valueOperations.get("refreshToken:" + request.username());
        if (savedRefreshToken == null || savedAccessToken == null) {
            throw new CustomException(AuthErrorCode.INVALID_JWT);
        }
        redisConfig.redisTemplate().delete("accessToken:" + request.username());
        redisConfig.redisTemplate().delete("refreshToken:" + request.username());
    }
}
