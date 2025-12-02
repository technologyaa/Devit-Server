package technologyaa.Devit.domain.project.dto;

import lombok.Builder;
import lombok.Getter;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.project.entity.Project;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProjectResponse {
    private Long projectId;
    private Long authorId;
    private String authorName;
    private String title;
    private String content;
    private Boolean isCompleted;
    private LocalDateTime createAt;

    public ProjectResponse(Project project) {
        this.projectId = project.getProjectId();
        this.title = project.getTitle();
        this.content = project.getContent();
        this.isCompleted = project.getIsCompleted();
        this.createAt = project.getCreateAt();
        this.authorId = project.getAuthor().getId();
        this.authorName = project.getAuthor().getUsername();
    }
}
