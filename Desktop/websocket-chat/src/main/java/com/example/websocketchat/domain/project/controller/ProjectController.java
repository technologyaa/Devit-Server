package com.example.websocketchat.domain.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.websocketchat.domain.project.dto.ProjectRequest;
import com.example.websocketchat.domain.project.entity.Project;
import com.example.websocketchat.domain.project.service.ProjectService;

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

    // read all
    @GetMapping
    public List<Project> findAll() {
        return projectService.findAll();
    }

    // read one
    @GetMapping("/{ProjectId}")
    public Project findOne(@PathVariable Long ProjectId) {
        return projectService.findOne(ProjectId);
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

