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

import technologyaa.Devit.domain.profile.dto.ProfileProjectResponse;
import technologyaa.Devit.domain.profile.dto.ProfileResponse;
import technologyaa.Devit.domain.profile.dto.request.UpdateRequest;
import technologyaa.Devit.domain.profile.dto.response.GetResponse;

import technologyaa.Devit.domain.project.repository.ProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    // ★ username 기반 현재 로그인 회원 조회
    private Member getCurrentMember() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        // auth.getName() == username
        String username = auth.getName();

        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
    }

    // 내 프로필 조회
    @Transactional(readOnly = true)
    public GetResponse getProfile() {
        return GetResponse.of(getCurrentMember());
    }

    // ID로 프로필 조회
    @Transactional(readOnly = true)
    public GetResponse getProfileById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        return GetResponse.of(member);
    }

    // 프로필 수정
    @Transactional
    public String updateProfile(UpdateRequest request) {
        Member member = getCurrentMember();
        member.setUsername(request.username());
        memberRepository.save(member);

        return member.getUsername();
    }

    // 프로필 삭제
    @Transactional
    public void deleteProfile() {
        Member member = getCurrentMember();
        memberRepository.delete(member);
    }

    // 기본 프로필 조회
    @Transactional(readOnly = true)
    public ProfileResponse getDefaultProfile() {

        // MemberRepository에서 첫 번째 회원을 기본 프로필로 사용
        Member member = memberRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

        // 예시: 프로젝트 히스토리는 그냥 빈 리스트로 처리
        List<ProfileProjectResponse> projectHistory = List.of();

        return ProfileResponse.builder()
                .name(member.getUsername())
                .job("기본 직업") // Member에 job 필드가 없으면 기본값
                .email(member.getEmail())
                .profileImageUrl("기본 이미지 URL") // Member에 프로필 이미지가 있으면 member.getProfileImageUrl()
                .completedProjects("0") // Member에 completedProjects 필드가 없으면 기본값
                .temperature("0") // Member에 temperature 필드가 없으면 기본값
                .projectList(projectHistory)
                .build();
    }
}