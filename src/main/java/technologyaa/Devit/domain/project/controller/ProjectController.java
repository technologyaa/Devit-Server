package technologyaa.Devit.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.common.ErrorResponse;
import technologyaa.Devit.domain.project.dto.ProjectCreateRequest;
import technologyaa.Devit.domain.project.dto.ProjectResponse;
import technologyaa.Devit.domain.project.dto.ProjectUpdateRequest;
import technologyaa.Devit.domain.project.dto.ProjectWithTasksResponse;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.auth.jwt.dto.response.MemberResponse;
import technologyaa.Devit.domain.project.service.ProjectService;
import technologyaa.Devit.global.util.SecurityUtil;

import java.io.IOException;
import java.util.List;

@Slf4j
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
    public ResponseEntity<?> createProject(
            @RequestBody ProjectCreateRequest request
    ) {
        try {
            // 1. 요청 검증
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                log.warn("프로젝트 생성 실패: 프로젝트 이름이 없습니다.");
                ErrorResponse errorResponse = ErrorResponse.of(
                        "BAD_REQUEST",
                        "프로젝트 이름을 입력해주세요."
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), errorResponse));
            }

            // 2. 인증 확인 및 사용자 정보 추출
            Member currentMember = securityUtil.getMember();
            Long authorId = currentMember.getId();

            // 3. 프로젝트 생성
            Long projectId = projectService.createProject(request, authorId);
            log.info("프로젝트 생성 성공: projectId={}, authorId={}", projectId, authorId);

            return ResponseEntity.status(HttpStatus.CREATED).body(projectId);

        } catch (DataAccessException e) {
            // 데이터베이스 오류
            log.error("프로젝트 생성 중 데이터베이스 오류 발생", e);
            ErrorResponse errorResponse = ErrorResponse.of(
                    "DATABASE_ERROR",
                    "데이터베이스 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse));

        } catch (Exception e) {
            // 기타 예외
            log.error("프로젝트 생성 중 예상치 못한 오류 발생", e);
            ErrorResponse errorResponse = ErrorResponse.of(
                    "INTERNAL_SERVER_ERROR",
                    "서버 내부 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse));
        }
    }

    // 전체 조회
    @Operation(summary = "전체 프로젝트 조회", description = "모든 프로젝트를 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getAllProjects() {
        try {
            log.info("전체 프로젝트 조회 요청");
            List<ProjectResponse> projects = projectService.findAllProjects();
            log.info("프로젝트 조회 성공: 프로젝트 수={}", projects.size());
            return ResponseEntity.ok(projects);

        } catch (DataAccessException e) {
            // 데이터베이스 오류
            log.error("프로젝트 목록 조회 중 데이터베이스 오류 발생", e);
            ErrorResponse errorResponse = ErrorResponse.of(
                    "DATABASE_ERROR",
                    "데이터베이스 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse));

        } catch (Exception e) {
            // 기타 예외
            log.error("프로젝트 목록 조회 중 예상치 못한 오류 발생", e);
            ErrorResponse errorResponse = ErrorResponse.of(
                    "INTERNAL_SERVER_ERROR",
                    "서버 내부 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse));
        }
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

