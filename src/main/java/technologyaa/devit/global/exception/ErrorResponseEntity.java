package technologyaa.devit.global.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import technologyaa.devit.global.exception.auth.AuthErrorCode;

@Data
@Builder
public class ErrorResponseEntity {
    private int status;
    private String name;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(AuthErrorCode e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus().value())
                        .name(e.name())
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build());
    }
}
