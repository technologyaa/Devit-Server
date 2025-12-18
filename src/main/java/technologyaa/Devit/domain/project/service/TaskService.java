package technologyaa.Devit.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.project.dto.TaskRequest;
import technologyaa.Devit.domain.project.dto.TaskResponse;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.entity.Task;
import technologyaa.Devit.domain.project.exception.ProjectErrorCode;
import technologyaa.Devit.domain.project.exception.ProjectException;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.project.repository.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public TaskResponse create(Long projectId, TaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));

        Task task = Task.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Task.TaskStatus.TODO)
                .build();

        Task savedTask = taskRepository.save(task);
        return TaskResponse.from(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAllByProjectId(Long projectId) {
        List<Task> tasks = taskRepository.findByProject_ProjectId(projectId);
        return tasks.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse findOne(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse update(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        Task updatedTask = taskRepository.save(task);
        return TaskResponse.from(updatedTask);
    }

    @Transactional
    public void delete(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}


