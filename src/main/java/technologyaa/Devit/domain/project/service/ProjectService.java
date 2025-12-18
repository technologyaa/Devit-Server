package technologyaa.Devit.domain.project.service;

import jakarta.mail.search.SearchTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.dto.response.MemberResponse;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.developer.entity.Developer;
import technologyaa.Devit.domain.developer.repository.DeveloperRepository;
import technologyaa.Devit.domain.project.dto.ProjectCreateRequest;
import technologyaa.Devit.domain.project.dto.ProjectResponse;
import technologyaa.Devit.domain.project.dto.ProjectUpdateRequest;
import technologyaa.Devit.domain.project.dto.ProjectWithTasksResponse;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.file.service.FileStorageService;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.entity.Task;
import technologyaa.Devit.domain.project.exception.AuthorErrorCode;
import technologyaa.Devit.domain.project.exception.AuthorException;
import technologyaa.Devit.domain.project.exception.ProjectErrorCode;
import technologyaa.Devit.domain.project.exception.ProjectException;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.project.repository.TaskRepository;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final FileStorageService fileStorageService;
    private final TaskRepository taskRepository;
    private final DeveloperRepository developerRepository;

    private void checkProjectAuthor(Project project, Long memberId) {
        if (!project.getAuthor().getId().equals(memberId)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }
    }

    // create
    @Transactional
    public Long createProject(ProjectCreateRequest request, Long authorId) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new AuthorException(AuthorErrorCode.AUTHOR_NOT_FOUND));

        Project project = Project.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .build();

        project.getMembers().add(author);

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
                    .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));
            return new ProjectResponse(project);
        }

        // update
        @Transactional
        public String updateProject(Long projectId, ProjectUpdateRequest request, Long memberId) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));

            checkProjectAuthor(project, memberId);

            project.setTitle(request.getTitle());
            project.setContent(request.getContent());
            project.setIsCompleted(request.getIsCompleted());

            return "프로젝트가 수정되었습니다.";
        }

    // delete
    @Transactional
    public void deleteProject(Long projectId, Long memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));

        checkProjectAuthor(project, memberId);

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


    @Transactional(readOnly = true)
    public List<MemberResponse> getProjectMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));

        return project.getMembers().stream()
                .map(member -> {
                    Developer developer = developerRepository.findById(member.getId())
                            .orElse(null);
                    return MemberResponse.from(member, developer != null ? developer.getMajor() : null);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectWithTasksResponse> getMyProjects(Long memberId) {
        List<Project> projects = projectRepository.findByMemberId(memberId);
        return projects.stream()
                .map(project -> {
                    List<Task> tasks = taskRepository.findByProject_ProjectId(project.getProjectId());
                    return ProjectWithTasksResponse.from(project, tasks);
                })
                .collect(Collectors.toList());
    }
}

