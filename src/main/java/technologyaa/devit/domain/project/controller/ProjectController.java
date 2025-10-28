package technologyaa.devit.domain.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import technologyaa.devit.domain.project.dto.ProjectRequest;
import technologyaa.devit.domain.project.entity.Project;
import technologyaa.devit.domain.project.service.ProjectService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    // create
    @PostMapping
    public Project create(@RequestBody ProjectRequest projectRequest) {
        return projectService.create(projectRequest);
    }

    // read
    @GetMapping
    public List<Project> findAll() {
        return projectService.findAll();
    }

    // update
    @PutMapping("/{ProjectId}")
    public Project update(@PathVariable Long ProjectId, @RequestBody ProjectRequest projectRequest) {
        return projectService.update(ProjectId, projectRequest);
    }

    // delete
    @DeleteMapping("/{ProjectId}")
    public void delete(@PathVariable Long ProjectId) {
        projectService.delete(ProjectId);
    }
}
