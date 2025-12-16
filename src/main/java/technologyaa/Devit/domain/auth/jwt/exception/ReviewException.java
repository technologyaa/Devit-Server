package technologyaa.Devit.domain.auth.jwt.exception;

import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {

    private final ReviewErrorCode errorCode;

    public ReviewException(ReviewErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ReviewException(ReviewErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}