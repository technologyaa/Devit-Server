package technologyaa.Devit.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.file.service.FileStorageService;
import technologyaa.Devit.domain.project.dto.ProjectRequest;
import technologyaa.Devit.domain.project.dto.ProjectWithTasksResponse;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.entity.Task;
import technologyaa.Devit.domain.project.exception.ProjectErrorCode;
import technologyaa.Devit.domain.project.exception.ProjectException;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.project.repository.TaskRepository;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final FileStorageService fileStorageService;
    private final MemberRepository memberRepository;

    // create
    public Project create(ProjectRequest request) {
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

    public APIResponse<List<ProjectWithTasksResponse>> getMyProjects() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        List<Project> projects = projectRepository.findByMemberId(member.getId());
        List<ProjectWithTasksResponse> projectResponses = projects.stream()
                .map(project -> {
                    List<Task> tasks = taskRepository.findByProject_ProjectId(project.getProjectId());
                    return ProjectWithTasksResponse.from(project, tasks);
                })
                .collect(java.util.stream.Collectors.toList());

        return APIResponse.ok(projectResponses);
    }

    public List<Member> getProjectMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));

        return List.copyOf(project.getMembers());
    }
}

