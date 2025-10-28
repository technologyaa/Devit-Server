package technologyaa.devit.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import technologyaa.devit.domain.project.dto.ProjectRequest;
import technologyaa.devit.domain.project.entity.Project;
import technologyaa.devit.domain.project.repository.ProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    // create
    public Project create(ProjectRequest request) {
        Project project = Project.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .major(request.getMajor())
                .build();
        return projectRepository.save(project);
    }

    // read
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    // update
    public Project update(Long ProjectId, ProjectRequest request) {
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
