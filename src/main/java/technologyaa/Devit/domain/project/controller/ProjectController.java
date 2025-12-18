package technologyaa.Devit.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.project.dto.ProjectCreateRequest;
import technologyaa.Devit.domain.project.dto.ProjectResponse;
import technologyaa.Devit.domain.project.dto.ProjectUpdateRequest;
import technologyaa.Devit.domain.project.dto.ProjectWithTasksResponse;
import technologyaa.Devit.domain.common.APIResponse;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.auth.jwt.dto.response.MemberResponse;
import technologyaa.Devit.domain.project.service.ProjectService;
import technologyaa.Devit.global.util.SecurityUtil;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Tag(name = "프로젝트 (Project)", description = "프로젝트 CRUD API")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final SecurityUtil securityUtil;

    // 생성
    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    @PostMapping
    public ResponseEntity<Long> createProject(
            @RequestBody ProjectCreateRequest request
    ) {
        Member currentMember = securityUtil.getMember();
        Long projectId = projectService.createProject(request, currentMember.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectId);
    }

    // 전체 조회
    @Operation(summary = "전체 프로젝트 조회", description = "모든 프로젝트를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.findAllProjects();
        return ResponseEntity.ok(projects);
    }

    // 추천 프로젝트 조회
    @Operation(summary = "추천 프로젝트 조회", description = "완료되지 않은 최신 프로젝트를 조회합니다.")
    @GetMapping("/recommended")
    public ResponseEntity<List<ProjectResponse>> getRecommendedProjects() {
        List<ProjectResponse> projects = projectService.getRecommendedProjects();
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
                                                @RequestBody ProjectUpdateRequest request) {
        Member currentMember = securityUtil.getMember();
        String responseMessage = projectService.updateProject(projectId, request, currentMember.getId());
        return ResponseEntity.ok(responseMessage);
    }

    // 삭제
    @Operation(summary = "프로젝트 삭제", description = "특정 프로젝트를 삭제합니다.")
    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable Long projectId) {
        Member currentMember = securityUtil.getMember();
        projectService.deleteProject(projectId, currentMember.getId());
    }

    @Operation(summary = "프로젝트 프로필 사진 변경", description = "프로젝트의 프로필 사진을 변경합니다.")
    @PutMapping("/profile/image/{id}")
    public APIResponse<?> uploadProfileImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return projectService.uploadProjectsImage(id, file);
    }

    @Operation(summary = "프로젝트 참여 멤버 조회", description = "특정 프로젝트에 참여 중인 모든 멤버를 조회합니다.")
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId) {
        List<MemberResponse> members = projectService.getProjectMembers(projectId);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "내 프로젝트 조회", description = "현재 사용자가 참여 중인 모든 프로젝트를 조회합니다.")
    @GetMapping("/my-projects")
    public APIResponse<List<ProjectWithTasksResponse>> getMyProjects() {
        Member currentMember = securityUtil.getMember();
        List<ProjectWithTasksResponse> projects = projectService.getMyProjects(currentMember.getId());
        return APIResponse.ok(projects);
    }
}

