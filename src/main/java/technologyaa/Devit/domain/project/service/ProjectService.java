package technologyaa.Devit.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.dto.response.MemberResponse;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
    public Long createProject(ProjectCreateRequest request, Long authorId, MultipartFile image) throws IOException {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new AuthorException(AuthorErrorCode.AUTHOR_NOT_FOUND));

        Project.ProjectBuilder projectBuilder = Project.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author);

        // 이미지가 있으면 업로드하고 profile에 저장
        if (image != null && !image.isEmpty()) {
            try {
                String imagePath = fileStorageService.storeProjectFile(image);
                projectBuilder.profile(imagePath);
                log.info("프로젝트 이미지 업로드 성공: {}", imagePath);
            } catch (IOException e) {
                log.error("프로젝트 이미지 업로드 실패", e);
                throw new RuntimeException("이미지 업로드 실패: " + e.getMessage(), e);
            }
        }

        Project project = projectBuilder.build();
        project.getMembers().add(author);

        Project savedProject = projectRepository.save(project);
        return savedProject.getProjectId();
    }

    // read all
    @Transactional(readOnly = true)
    public List<ProjectResponse> findAllProjects() {
        try {
            log.info("프로젝트 목록 조회 시작");
            List<Project> projects = projectRepository.findAllWithAuthor();
            log.info("조회된 프로젝트 수: {}", projects.size());

            // 트랜잭션 내에서 모든 데이터를 로드하여 변환
            List<ProjectResponse> result = new ArrayList<>();
            for (Project project : projects) {
                try {
                    if (project == null) {
                        log.warn("null 프로젝트 발견, 건너뜀");
                        continue;
                    }

                    // 각 프로젝트의 author가 제대로 로드되었는지 확인
                    if (project.getAuthor() == null) {
                        log.warn("프로젝트 author가 null입니다 - projectId: {}, 건너뜀", project.getProjectId());
                        continue;
                    }

                    // author의 username을 미리 호출하여 lazy loading 트리거 (JOIN FETCH로 이미 로드되어 있어야 함)
                    try {
                        project.getAuthor().getUsername();
                    } catch (Exception e) {
                        log.error("author 접근 중 오류 발생 - projectId: {}", project.getProjectId(), e);
                        continue;
                    }

                    result.add(ProjectResponse.from(project));
                } catch (Exception e) {
                    log.error("프로젝트 변환 중 오류 발생 - projectId: {}",
                            project != null ? project.getProjectId() : "unknown", e);
                    // 개별 프로젝트 변환 실패 시 해당 프로젝트만 건너뛰고 계속 진행
                    continue;
                }
            }

            log.info("변환된 프로젝트 수: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("프로젝트 목록 조회 중 오류 발생", e);
            throw new RuntimeException("프로젝트 목록 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // read one
    @Transactional(readOnly = true)
    public ProjectResponse findProjectById(Long id) {
        Project project = projectRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));

        // author의 major 가져오기
        String major = null;
        Developer developer = developerRepository.findById(project.getAuthor().getId()).orElse(null);
        if (developer != null && developer.getMajor() != null) {
            major = developer.getMajor().toString();
        }

        return ProjectResponse.from(project, major);
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
    public List<ProjectWithTasksResponse> getMyProjects() {
        Long memberId = getCurrentMember().getId();
        List<Project> projects = projectRepository.findByMemberId(memberId);
        return projects.stream()
                .map(project -> {
                    List<Task> tasks = taskRepository.findByProject_ProjectId(project.getProjectId());
                    return ProjectWithTasksResponse.from(project, tasks);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getRecommendedProjects() {
        return projectRepository.findRecommendedProjects().stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    private Member getCurrentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        String username = auth.getName();
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
    }
}

