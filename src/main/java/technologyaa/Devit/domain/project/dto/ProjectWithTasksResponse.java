package technologyaa.Devit.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.entity.Task;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectWithTasksResponse {
    private Long projectId;
    private String title;
    private String content;
    private String major;
    private String profile;
    private List<TaskResponse> tasks;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskResponse {
        private Long taskId;
        private String title;
        private String description;
        private String status;
    }

    public static ProjectWithTasksResponse from(Project project, List<Task> tasks) {
        return ProjectWithTasksResponse.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle())
                .content(project.getContent())
                .major(null) // Project 엔티티에 major 필드가 없으므로 null 처리
                .profile(project.getProfile())
                .tasks(tasks.stream()
                        .map(task -> TaskResponse.builder()
                                .taskId(task.getTaskId())
                                .title(task.getTitle())
                                .description(task.getDescription())
                                .status(task.getStatus() != null ? task.getStatus().toString() : null)
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

