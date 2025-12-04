package technologyaa.Devit.domain.project.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthorErrorCode {
    AUTHOR_NOT_FOUND(404, "AUTHOR_NOT_FOUND", "해당 작성자를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
