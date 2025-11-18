package technologyaa.Devit.domain.auth.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 로그인 실패 시 처리하는 핸들러
 */
@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                       AuthenticationException exception) throws IOException, ServletException {
        
        log.error("OAuth2 로그인 실패 - 에러: {}", exception.getMessage(), exception);
        
        // 로그인 실패 시 로그인 페이지로 리다이렉트하고 에러 파라미터 추가
        String errorMessage = java.net.URLEncoder.encode(
                exception.getMessage() != null ? exception.getMessage() : "알 수 없는 오류",
                java.nio.charset.StandardCharsets.UTF_8);
        setDefaultFailureUrl("/login?error=true&message=" + errorMessage);
        super.onAuthenticationFailure(request, response, exception);
    }
}

