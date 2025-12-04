package technologyaa.Devit.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.project.dto.TaskRequest;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.entity.Task;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.project.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public Task create(Long projectId, TaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("해당 프로젝트를 찾을 수 없습니다."));

        Task task = Task.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Task.TaskStatus.TODO)
                .build();

        return taskRepository.save(task);
    }

    public List<Task> findAllByProjectId(Long projectId) {
        return taskRepository.findByProject_ProjectId(projectId);
    }

    public Task findOne(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("해당 업무를 찾을 수 없습니다."));
    }

    public Task update(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("해당 업무를 찾을 수 없습니다."));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        return taskRepository.save(task);
    }

    public void delete(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}


