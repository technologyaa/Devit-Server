package technologyaa.Devit.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.common.ErrorResponse;

import java.io.IOException;

/**
 * Spring Security에서 인증되지 않은 사용자가 인증이 필요한 리소스에 접근할 때 호출되는 핸들러
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.error("Unauthorized error: {}", authException.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "UNAUTHORIZED",
                "인증이 필요합니다."
        );

        APIResponse<ErrorResponse> apiResponse = new APIResponse<>(
                HttpServletResponse.SC_UNAUTHORIZED,
                errorResponse
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}