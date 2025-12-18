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
    
    // 프론트엔드 호환성을 위한 필드
    private String thumbnail;  // profile 필드의 별칭
    private String major;       // author의 major (Developer에서 가져옴)
    private String owner;       // authorName의 별칭
    private LocalDateTime createdAt;  // createAt의 별칭
    private LocalDateTime updatedAt;  // createAt 사용 (updatedAt 필드가 없을 경우)

    public static ProjectResponse from(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        
        if (project.getAuthor() == null) {
            throw new IllegalArgumentException("Project author cannot be null for projectId: " + project.getProjectId());
        }
        
        LocalDateTime updatedAt = project.getUpdatedAt() != null ? project.getUpdatedAt() : project.getCreateAt();
        String authorUsername = project.getAuthor().getUsername();
        
        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .authorId(project.getAuthor().getId())
                .authorName(authorUsername)
                .title(project.getTitle())
                .content(project.getContent())
                .isCompleted(project.getIsCompleted())
                .createAt(project.getCreateAt())
                .thumbnail(project.getProfile())
                .owner(authorUsername)
                .createdAt(project.getCreateAt())
                .updatedAt(updatedAt)
                .build();
    }
    
    public static ProjectResponse from(Project project, String major) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        
        if (project.getAuthor() == null) {
            throw new IllegalArgumentException("Project author cannot be null for projectId: " + project.getProjectId());
        }
        
        LocalDateTime updatedAt = project.getUpdatedAt() != null ? project.getUpdatedAt() : project.getCreateAt();
        String authorUsername = project.getAuthor().getUsername();
        
        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .authorId(project.getAuthor().getId())
                .authorName(authorUsername)
                .title(project.getTitle())
                .content(project.getContent())
                .isCompleted(project.getIsCompleted())
                .createAt(project.getCreateAt())
                .thumbnail(project.getProfile())
                .major(major)
                .owner(authorUsername)
                .createdAt(project.getCreateAt())
                .updatedAt(updatedAt)
                .build();
    }
}
