package com.example.websocketchat.domain.project.controller;

import com.example.websocketchat.domain.project.dto.ProjectCreateRequest;
import com.example.websocketchat.domain.project.dto.ProjectDetailResponse;
import com.example.websocketchat.domain.project.dto.ProjectSummaryResponse;
import com.example.websocketchat.domain.project.dto.ProjectTaskCreateRequest;
import com.example.websocketchat.domain.project.dto.ProjectTaskStatusUpdateRequest;
import com.example.websocketchat.domain.project.dto.ProjectUpdateRequest;
import com.example.websocketchat.domain.project.service.ProjectService;
import com.example.websocketchat.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "프로젝트 (Project)", description = "프론트엔드 사양에 맞춘 프로젝트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> create(@Valid @RequestBody ProjectCreateRequest request) {
        ProjectDetailResponse response = projectService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, HttpStatus.CREATED.value(), "프로젝트가 생성되었습니다.", response));
    }

    @Operation(summary = "프로젝트 전체 조회", description = "모든 프로젝트를 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectSummaryResponse>>> findAll() {
        List<ProjectSummaryResponse> response = projectService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "프로젝트 목록을 조회했습니다.", response));
    }

    @Operation(summary = "프로젝트 단일 조회", description = "특정 프로젝트 상세 정보를 조회합니다.")
    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> findOne(@PathVariable Long projectId) {
        ProjectDetailResponse response = projectService.findOne(projectId);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "프로젝트 상세 정보를 조회했습니다.", response));
    }

    @Operation(summary = "프로젝트 수정", description = "특정 프로젝트를 수정합니다.")
    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> update(@PathVariable Long projectId,
                                                                     @Valid @RequestBody ProjectUpdateRequest request) {
        ProjectDetailResponse response = projectService.update(projectId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "프로젝트가 수정되었습니다.", response));
    }

    @Operation(summary = "프로젝트 삭제", description = "특정 프로젝트를 삭제합니다.")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long projectId) {
        projectService.delete(projectId);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "프로젝트가 삭제되었습니다.", null));
    }

    @Operation(summary = "업무 추가", description = "프로젝트에 새로운 업무를 추가합니다.")
    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> addTask(@PathVariable Long projectId,
                                                                      @Valid @RequestBody ProjectTaskCreateRequest request) {
        ProjectDetailResponse response = projectService.addTask(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, HttpStatus.CREATED.value(), "업무가 추가되었습니다.", response));
    }

    @Operation(summary = "업무 상태 변경", description = "업무 완료 여부를 업데이트합니다.")
    @PostMapping("/{projectId}/tasks/{taskId}/status")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> updateTaskStatus(@PathVariable Long projectId,
                                                                               @PathVariable Long taskId,
                                                                               @Valid @RequestBody ProjectTaskStatusUpdateRequest request) {
        ProjectDetailResponse response = projectService.updateTaskStatus(projectId, taskId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "업무 상태가 변경되었습니다.", response));
    }

    @Operation(summary = "업무 삭제", description = "프로젝트에서 업무를 삭제합니다.")
    @DeleteMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> deleteTask(@PathVariable Long projectId,
                                                                         @PathVariable Long taskId) {
        ProjectDetailResponse response = projectService.deleteTask(projectId, taskId);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "업무가 삭제되었습니다.", response));
    }
}

