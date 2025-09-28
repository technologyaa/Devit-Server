package technologyaa.devit.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import technologyaa.devit.global.exception.auth.AuthErrorCode;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    AuthErrorCode errorCode;
}
