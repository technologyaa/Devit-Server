package technologyaa.Devit.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.project.dto.ProjectCreateRequest;
import technologyaa.Devit.domain.project.dto.ProjectResponse;
import technologyaa.Devit.domain.project.dto.ProjectUpdateRequest;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.file.service.FileStorageService;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.exception.ProjectErrorCode;
import technologyaa.Devit.domain.project.exception.ProjectException;
import technologyaa.Devit.domain.project.repository.ProjectRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final FileStorageService fileStorageService;

    // create
    @Transactional
    public Long createProject(ProjectCreateRequest request, Long authorId) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 작성자를 찾을 수 없습니다"));

        Project project = Project.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .isCompleted(false)
                .build();

        Project savedProject = projectRepository.save(project);
        return savedProject.getProjectId();
    }

    // read all
    @Transactional(readOnly = true)
    public List<ProjectResponse> findAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }

    // read one
    @Transactional(readOnly = true)
    public ProjectResponse findProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다."));
        return new ProjectResponse(project);
    }

    // update
    @Transactional
    public String updateProject(Long projectId, ProjectUpdateRequest request, Long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다."));

        if (!project.getAuthor().getId().equals(memberId)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }
        project.setTitle(request.getTitle());
        project.setContent(request.getContent());
        project.setIsCompleted(request.getIsCompleted());

        return null;
    }

    // delete
    @Transactional
    public void deleteProject(Long projectId, Long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다."));

        // **권한 확인 로직**
        if (!project.getAuthor().getId().equals(memberId)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }


        projectRepository.delete(project);
    }

    public APIResponse<?> uploadProjectsImage(Long id,MultipartFile file) throws IOException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));

        if(project.getProfile() != null) {
            fileStorageService.deleteFile(project.getProfile());
        }

        try {
            String imagePath = fileStorageService.storeProjectFile(file);
            project.setProfile(imagePath);
            projectRepository.save(project);
            return APIResponse.ok(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }
    }
}

