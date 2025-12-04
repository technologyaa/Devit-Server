package technologyaa.Devit.domain.project.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProjectErrorCode {
    PROJECT_NOT_FOUND(404, "PROJECT_NOT_FOUND", "프로젝트를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
