package technologyaa.Devit.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.project.dto.TaskRequest;
import technologyaa.Devit.domain.project.dto.TaskResponse;
import technologyaa.Devit.domain.project.service.TaskService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks")
@Tag(name = "업무 (Task)", description = "프로젝트 업무 CRUD API")
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "업무 생성", description = "특정 프로젝트에 새로운 업무를 생성합니다.")
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @Parameter(description = "프로젝트 ID", required = true, example = "1")
            @PathVariable Long projectId, 
            @RequestBody TaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.create(projectId, taskRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @Operation(summary = "프로젝트의 모든 업무 조회", description = "특정 프로젝트의 모든 업무를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAll(
            @Parameter(description = "프로젝트 ID", required = true, example = "1")
            @PathVariable Long projectId) {
        List<TaskResponse> tasks = taskService.findAllByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "업무 단일 조회", description = "특정 업무를 조회합니다.")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> findOne(
            @Parameter(description = "프로젝트 ID", required = true, example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "업무 ID", required = true, example = "1")
            @PathVariable Long taskId) {
        TaskResponse taskResponse = taskService.findOne(taskId);
        return ResponseEntity.ok(taskResponse);
    }

    @Operation(summary = "업무 수정", description = "특정 업무를 수정합니다.")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> update(
            @Parameter(description = "프로젝트 ID", required = true, example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "업무 ID", required = true, example = "1")
            @PathVariable Long taskId, 
            @RequestBody TaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.update(taskId, taskRequest);
        return ResponseEntity.ok(taskResponse);
    }

    @Operation(summary = "업무 삭제", description = "특정 업무를 삭제합니다.")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "프로젝트 ID", required = true, example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "업무 ID", required = true, example = "1")
            @PathVariable Long taskId) {
        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}

