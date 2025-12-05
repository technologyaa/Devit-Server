package technologyaa.Devit.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.project.dto.ProjectCreateRequest;
import technologyaa.Devit.domain.project.dto.ProjectResponse;
import technologyaa.Devit.domain.project.dto.ProjectUpdateRequest;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.project.service.ProjectService;

import java.io.IOException;
import java.util.List;

@Tag(name = "프로젝트 (Project)", description = "프로젝트 CRUD API")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // 생성
    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    @PostMapping
    public ResponseEntity<Long> createProject(
            @RequestBody ProjectCreateRequest request,
            @RequestParam("memberId") Long authorId
    ) {
        Long projectId = projectService.createProject(request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectId);
    }

    // 전체 조회
    @Operation(summary = "전체 프로젝트 조회", description = "모든 프로젝트를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.findAllProjects();
        return ResponseEntity.ok(projects);
    }

    // 조회 (하나)
    @Operation(summary = "프로젝트 단일 조회", description = "특정 프로젝트를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse project = projectService.findProjectById(id);
        return ResponseEntity.ok(project);
    }

    // 수정
    @Operation(summary = "프로젝트 수정", description = "특정 프로젝트를 수정합니다.")
    @PutMapping("/{projectId}")
    public ResponseEntity<String> updateProject(@Parameter(description = "프로젝트 ID", required = true, example = "1")
                                                @PathVariable Long projectId,
                                                @RequestBody ProjectUpdateRequest request,
                                                @RequestParam("memberId") Long memberId) {
        String responseMessage = projectService.updateProject(projectId, request, memberId);
        return ResponseEntity.ok(responseMessage);
    }

    // 삭제
    @Operation(summary = "프로젝트 삭제", description = "특정 프로젝트를 삭제합니다.")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId,
                                              @RequestParam("memberId") Long memberId) {
        projectService.deleteProject(projectId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "프로젝트 프로필 사진 변경", description = "프로젝트의 프로필 사진을 변경합니다.")
    @PutMapping("/profile/image/{id}")
    public APIResponse<?> uploadProfileImage(
            @PathVariable Long id,
            @PathVariable("file")MultipartFile file
    ) throws IOException {
        return projectService.uploadProjectsImage(id, file);
    }
}

