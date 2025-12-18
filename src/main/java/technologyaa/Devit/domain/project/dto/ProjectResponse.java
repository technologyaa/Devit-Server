package technologyaa.Devit.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import technologyaa.Devit.domain.project.entity.Project;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class ProjectResponse {
    private Long projectId;
    private Long authorId;
    private String authorName;
    private String title;
    private String content;
    private Boolean isCompleted;
    private LocalDateTime createAt;

    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .authorId(project.getAuthor().getId())
                .authorName(project.getAuthor().getUsername())
                .title(project.getTitle())
                .content(project.getContent())
                .isCompleted(project.getIsCompleted())
                .createAt(project.getCreateAt())
                .build();
    }
}
