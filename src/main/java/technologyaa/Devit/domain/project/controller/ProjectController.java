package technologyaa.Devit.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.project.dto.ProjectRequest;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.service.ProjectService;

import java.util.List;

@Tag(name = "프로젝트 (Project)", description = "프로젝트 CRUD API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성합니다.")
    @PostMapping
    public Project create(@RequestBody ProjectRequest projectRequest) {
        return projectService.create(projectRequest);
    }

    @Operation(summary = "프로젝트 전체 조회", description = "모든 프로젝트를 조회합니다.")
    @GetMapping
    public List<Project> findAll() {
        return projectService.findAll();
    }

    @Operation(summary = "프로젝트 단일 조회", description = "특정 프로젝트를 조회합니다.")
    @GetMapping("/{projectId}")
    public Project findOne(@PathVariable Long projectId) {
        return projectService.findOne(projectId);
    }

    @Operation(summary = "프로젝트 수정", description = "특정 프로젝트를 수정합니다.")
    @PutMapping("/{projectId}")
    public Project update(@PathVariable Long projectId, @RequestBody ProjectRequest projectRequest) {
        return projectService.update(projectId, projectRequest);
    }

    @Operation(summary = "프로젝트 삭제", description = "특정 프로젝트를 삭제합니다.")
    @DeleteMapping("/{projectId}")
    public void delete(@PathVariable Long projectId) {
        projectService.delete(projectId);
    }
}

