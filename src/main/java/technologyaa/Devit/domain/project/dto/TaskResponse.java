package technologyaa.Devit.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import technologyaa.Devit.domain.project.entity.Task;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class TaskResponse {
    private Long taskId;
    private String title;
    private String description;
    private Boolean isDone;  // status가 DONE이면 true
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 원본 status도 포함 (선택사항)
    private String status;

    public static TaskResponse from(Task task) {
        boolean isDone = task.getStatus() != null && task.getStatus() == Task.TaskStatus.DONE;
        LocalDateTime createdAt = task.getCreatedAt() != null ? task.getCreatedAt() : null;
        LocalDateTime updatedAt = task.getUpdatedAt() != null ? task.getUpdatedAt() : createdAt;
        
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .isDone(isDone)
                .projectId(task.getProject().getProjectId())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .status(task.getStatus() != null ? task.getStatus().toString() : null)
                .build();
    }
}




