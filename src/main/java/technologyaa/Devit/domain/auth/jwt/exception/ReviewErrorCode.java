package technologyaa.Devit.domain.auth.jwt.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode {

    // 공통
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    PROJECT_NOT_FOUND(404, "PROJECT_NOT_FOUND", "프로젝트를 찾을 수 없습니다."),

    // 리뷰 관련
    INVALID_RATING(400, "INVALID_RATING", "평점은 0~5 사이여야 합니다."),
    FORBIDDEN_REVIEW(403, "FORBIDDEN_REVIEW", "프로젝트 참여자만 리뷰할 수 있습니다."),
    DUPLICATE_REVIEW(409, "DUPLICATE_REVIEW", "이미 해당 프로젝트에 대한 리뷰가 존재합니다.");

    private final int status;
    private final String code;
    private final String message;
}