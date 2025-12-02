package technologyaa.Devit.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.project.dto.ProjectCreateRequest;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.repository.ProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    // create
    public Project create(ProjectCreateRequest request) {
        Project project = Project.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .major(request.getMajor())
                .build();
        return projectRepository.save(project);
    }

    // read all
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    // read one
    public Project findOne(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("해당 프로젝트를 찾을 수 없습니다."));
    }

    // update
    public Project update(Long ProjectId, ProjectCreateRequest request) {
        Project project = projectRepository.findById(ProjectId)
                .orElseThrow(() -> new RuntimeException("해당 프로젝트를 찾을 수 없습니다."));
        project.setTitle(request.getTitle());
        project.setContent(request.getContent());
        project.setMajor(request.getMajor());
        return projectRepository.save(project);
    }

    // delete
    public void delete(Long ProjectId) {
        projectRepository.deleteById(ProjectId);
    }
}

