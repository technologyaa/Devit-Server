package technologyaa.Devit.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.common.ErrorResponse;
import technologyaa.Devit.domain.project.exception.ProjectException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 인증/인가 관련 예외 처리
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> handleAuthException(AuthException e) {
        log.error("AuthException: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getAuthErrorCode().getCode(),
                e.getAuthErrorCode().getMessage()
        );

        return ResponseEntity
                .status(e.getAuthErrorCode().getStatus())
                .body(new APIResponse<>(e.getAuthErrorCode().getStatus(), errorResponse));
    }

    /**
     * 프로젝트 관련 예외 처리
     */
    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> handleProjectException(ProjectException e) {
        log.error("ProjectException: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getProjectErrorCode().getCode(),
                e.getProjectErrorCode().getMessage()
        );

        return ResponseEntity
                .status(e.getProjectErrorCode().getStatus())
                .body(new APIResponse<>(e.getProjectErrorCode().getStatus(), errorResponse));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("NoResourceFoundException: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "NOT_FOUND",
                "존재하지 않는 엔드포인트입니다."
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), errorResponse));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<APIResponse<ErrorResponse>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "NOT_FOUND",
                "요청한 리소스를 찾을 수 없습니다."
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), errorResponse));
    }

    /**
     * Validation 예외 처리 (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), errors));
    }

    /**
     * 기타 모든 예외 처리 (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<ErrorResponse>> handleException(Exception e) {
        log.error("Unexpected error occurred: ", e);

        ErrorResponse errorResponse = ErrorResponse.of(
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse));
    }
}