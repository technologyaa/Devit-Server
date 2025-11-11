package com.example.websocketchat.domain.project.service;

import com.example.websocketchat.domain.project.dto.ProjectCreateRequest;
import com.example.websocketchat.domain.project.dto.ProjectDetailResponse;
import com.example.websocketchat.domain.project.dto.ProjectSummaryResponse;
import com.example.websocketchat.domain.project.dto.ProjectTaskCreateRequest;
import com.example.websocketchat.domain.project.dto.ProjectTaskResponse;
import com.example.websocketchat.domain.project.dto.ProjectTaskStatusUpdateRequest;
import com.example.websocketchat.domain.project.dto.ProjectUpdateRequest;
import com.example.websocketchat.domain.project.entity.Project;
import com.example.websocketchat.domain.project.entity.ProjectTask;
import com.example.websocketchat.domain.project.repository.ProjectRepository;
import com.example.websocketchat.domain.project.repository.ProjectTaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectTaskRepository projectTaskRepository;

    public ProjectDetailResponse create(ProjectCreateRequest request) {
        Project project = Project.builder()
                .title(request.title())
                .description(request.description())
                .ownerName(request.ownerName())
                .thumbnailUrl(Optional.ofNullable(request.thumbnailUrl()).orElse("/assets/dummy-thumbnail.svg"))
                .major(request.major())
                .creditBudget(Optional.ofNullable(request.creditBudget()).orElse(0L))
                .build();

        Project saved = projectRepository.save(project);
        return toDetailResponse(saved);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ProjectSummaryResponse> findAll() {
        return projectRepository.findAll().stream()
                .sorted(Comparator.comparing(Project::getCreatedAt).reversed())
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public ProjectDetailResponse findOne(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다."));
        return toDetailResponse(project);
    }

    public ProjectDetailResponse update(Long projectId, ProjectUpdateRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다."));

        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setOwnerName(request.ownerName());

        if (Objects.nonNull(request.thumbnailUrl())) {
            project.setThumbnailUrl(request.thumbnailUrl());
        }

        project.setMajor(request.major());

        if (Objects.nonNull(request.creditBudget())) {
            project.setCreditBudget(request.creditBudget());
        }

        return toDetailResponse(project);
    }

    public void delete(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    public ProjectDetailResponse addTask(Long projectId, ProjectTaskCreateRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다."));

        int nextOrder = projectTaskRepository
                .findAllByProject_ProjectIdOrderBySortOrderAscTaskIdAsc(projectId)
                .size();

        ProjectTask task = ProjectTask.builder()
                .title(request.title())
                .description(request.description())
                .creditReward(Optional.ofNullable(request.creditReward()).orElse(1000L))
                .sortOrder(nextOrder)
                .build();

        project.addTask(task);
        Project saved = projectRepository.save(project);

        return toDetailResponse(saved);
    }

    public ProjectDetailResponse updateTaskStatus(Long projectId, Long taskId, ProjectTaskStatusUpdateRequest request) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("해당 업무를 찾을 수 없습니다."));

        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("프로젝트와 업무가 일치하지 않습니다.");
        }

        task.markDone(request.done());
        return toDetailResponse(task.getProject());
    }

    public ProjectDetailResponse deleteTask(Long projectId, Long taskId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("해당 업무를 찾을 수 없습니다."));

        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("프로젝트와 업무가 일치하지 않습니다.");
        }

        Project project = task.getProject();
        project.removeTask(task);
        projectTaskRepository.delete(task);

        return toDetailResponse(project);
    }

    private ProjectSummaryResponse toSummaryResponse(Project project) {
        return ProjectSummaryResponse.builder()
                .id(project.getProjectId())
                .title(project.getTitle())
                .description(project.getDescription())
                .ownerName(project.getOwnerName())
                .thumbnailUrl(project.getThumbnailUrl())
                .creditBudget(project.getCreditBudget())
                .createdAt(project.getCreatedAt())
                .build();
    }

    private ProjectDetailResponse toDetailResponse(Project project) {
        List<ProjectTaskResponse> taskResponses = projectTaskRepository
                .findAllByProject_ProjectIdOrderBySortOrderAscTaskIdAsc(project.getProjectId())
                .stream()
                .map(this::toTaskResponse)
                .toList();

        return ProjectDetailResponse.builder()
                .id(project.getProjectId())
                .title(project.getTitle())
                .description(project.getDescription())
                .major(project.getMajor())
                .ownerName(project.getOwnerName())
                .thumbnailUrl(project.getThumbnailUrl())
                .creditBudget(project.getCreditBudget())
                .createdAt(project.getCreatedAt())
                .tasks(taskResponses)
                .build();
    }

    private ProjectTaskResponse toTaskResponse(ProjectTask task) {
        return ProjectTaskResponse.builder()
                .id(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .isDone(Boolean.TRUE.equals(task.getDone()))
                .sortOrder(task.getSortOrder())
                .creditReward(task.getCreditReward())
                .createdAt(task.getCreatedAt())
                .build();
    }
}

