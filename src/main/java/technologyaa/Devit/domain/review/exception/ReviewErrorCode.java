package technologyaa.Devit.domain.review.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode {
    MEMBER_NOT_IN_PROJECT(HttpStatus.NOT_FOUND.value(), "MEMBER_NOT_IN_PROJECT", "해당 프로젝트에 속해 있지 않은 멤버입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "INVALID_INPUT_VALUE", "잘못된 값이 입력되었습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "REVIEW_NOT_FOUND", "리뷰를 찾을 수 없습니다."),
    UNAUTHORIZED_REVIEW_ACCESS(HttpStatus.FORBIDDEN.value(), "UNAUTHORIZED_REVIEW_ACCESS", "해당 리뷰에 대한 권한이 없습니다.");


    private final int status;
    private final String code;
    private final String message;
}
