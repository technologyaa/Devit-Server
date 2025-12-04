package technologyaa.Devit.domain.project.exception;

import lombok.Getter;

@Getter
public class ProjectException extends RuntimeException {
    private final ProjectErrorCode projectErrorCode;

    public ProjectException(ProjectErrorCode projectErrorCode) {
        super(projectErrorCode.getMessage());
        this.projectErrorCode = projectErrorCode;
    }

    public ProjectException(ProjectErrorCode projectErrorCode, String message) {
        super(message);
        this.projectErrorCode = projectErrorCode;
    }
}
