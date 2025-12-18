package technologyaa.Devit.domain.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.profile.dto.request.UpdateRequest;
import technologyaa.Devit.domain.profile.dto.response.GetResponse;
import technologyaa.Devit.domain.profile.dto.response.ProfileResponse;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.entity.Task;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.project.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    /** 현재 로그인 사용자 조회 */
    private Member getCurrentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        return memberRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
    }

    /** 완료 프로젝트 개수 계산 */
    private long countCompletedProjects(Member member) {
        List<Project> projects = projectRepository.findByMemberId(member.getId());

        return projects.stream()
                .filter(project -> {
                    Long projectId = project.getProjectId();

                    long totalTasks =
                            taskRepository.countByProject_ProjectId(projectId);

                    // Task 없는 프로젝트는 제외
                    if (totalTasks == 0) return false;

                    long notDoneTasks =
                            taskRepository.countByProject_ProjectIdAndStatusNot(
                                    projectId,
                                    Task.TaskStatus.DONE
                            );

                    return notDoneTasks == 0;
                })
                .count();
    }

    /** 내 프로필 조회 */
    @Transactional(readOnly = true)
    public APIResponse<ProfileResponse> getProfile() {
        Member member = getCurrentMember();
        long completedProjectCount = countCompletedProjects(member);

        return APIResponse.ok(
                ProfileResponse.of(member, completedProjectCount)
        );
    }

    /** 특정 유저 프로필 조회 */
    @Transactional(readOnly = true)
    public APIResponse<GetResponse> getProfileById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        long completedProjectCount = countCompletedProjects(member);

        return APIResponse.ok(
                GetResponse.of(member, completedProjectCount)
        );
    }

    /** 프로필 수정 */
    public APIResponse<ProfileResponse> updateProfile(UpdateRequest request) {
        Member member = getCurrentMember();

        if (request.username() != null
                && !request.username().equals(member.getUsername())) {

            boolean exists = memberRepository.existsByUsername(request.username());
            if (exists) {
                throw new AuthException(AuthErrorCode.USERNAME_ALREADY_EXISTS);
            }
            member.setUsername(request.username());
        }

        memberRepository.save(member);

        long completedProjectCount = countCompletedProjects(member);

        return APIResponse.ok(
                ProfileResponse.of(member, completedProjectCount)
        );
    }

    /** 프로필 삭제 */
    public APIResponse<Void> deleteProfile() {
        Member member = getCurrentMember();
        memberRepository.delete(member);
        return APIResponse.ok(null);
    }
}