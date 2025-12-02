package technologyaa.Devit.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.project.dto.ProjectCreateRequest;
import technologyaa.Devit.domain.project.dto.ProjectResponse;
import technologyaa.Devit.domain.project.dto.ProjectUpdateRequest;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.service.ProjectService;

import java.util.List;

@Tag(name = "프로젝트 (Project)", description = "프로젝트 CRUD API")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // 생성
    @PostMapping
    public ResponseEntity<Long> createProject(@RequestBody ProjectCreateRequest request,
                                              @RequestParam("memberId") Long authorId) {
        Long projectId = projectService.createProject(request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectId);
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.findAllProjects();
        return ResponseEntity.ok(projects);
    }

    // 조회 (하나)
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse project = projectService.findProjectById(id);
        return ResponseEntity.ok(project);
    }

    // 수정
    @PutMapping("/{projectId}")
    public ResponseEntity<String> updateProject(@PathVariable Long projectId,
                                                @RequestBody ProjectUpdateRequest request,
                                                @RequestParam("memberId") Long memberId) {
        String responseMessage = projectService.updateProject(projectId, request, memberId);
        return ResponseEntity.ok(responseMessage);
    }

    // 삭제
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId,
                                              @RequestParam("memberId") Long memberId) {
        projectService.deleteProject(projectId, memberId);
        return ResponseEntity.noContent().build();
    }
}

