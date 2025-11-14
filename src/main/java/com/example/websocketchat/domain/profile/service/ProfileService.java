package com.example.websocketchat.domain.profile.service;

import com.example.websocketchat.domain.member.entity.Member;
import com.example.websocketchat.domain.member.exception.AuthErrorCode;
import com.example.websocketchat.domain.member.repository.MemberRepository;
import com.example.websocketchat.domain.profile.dto.request.Updaterequest;
import com.example.websocketchat.domain.profile.dto.response.GetProfileRes;
import com.example.websocketchat.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.websocketchat.domain.developer.entity.DeveloperProfile;
import com.example.websocketchat.domain.developer.repository.DeveloperProfileRepository;
import com.example.websocketchat.domain.profile.dto.ProfileProjectResponse;
import com.example.websocketchat.domain.profile.dto.ProfileResponse;
import com.example.websocketchat.domain.project.entity.Project;
import com.example.websocketchat.domain.project.repository.ProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final MemberRepository memberRepository;
    private final DeveloperProfileRepository developerProfileRepository;
    private final ProjectRepository projectRepository;

    // 현재 로그인한 사용자를 가져오는 유틸 메서드
    private Member getCurrentMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
        }

        String username = auth.getName();
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public GetProfileRes getProfile() {
        Member member = getCurrentMember();
        return GetProfileRes.of(member);
    }

    @Transactional(readOnly = true)
    public GetProfileRes getProfileById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        return GetProfileRes.of(member);
    }

    @Transactional
    public String updateProfile(Updaterequest request) {
        Member member = getCurrentMember();
        member.UpdateMember(request.username());
        memberRepository.save(member);
        return member.getUsername();
    }

    @Transactional
    public void deleteProfile() {
        Member member = getCurrentMember();
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getDefaultProfile() {
        DeveloperProfile developer = developerProfileRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("등록된 개발자 프로필이 없습니다."));

        List<Project> projects = projectRepository.findAll();

        List<ProfileProjectResponse> projectHistory = projects.stream()
                .limit(5)
                .map(project -> ProfileProjectResponse.builder()
                        .name(project.getTitle())
                        .points("+" + project.getCreditBudget())
                        .build())
                .toList();

        return ProfileResponse.builder()
                .name(developer.getName())
                .job(developer.getJob())
                .email(developer.getEmail())
                .profileImageUrl(developer.getProfileImageUrl())
                .completedProjects(String.valueOf(developer.getCompletedProjects()))
                .temperature(String.valueOf(developer.getTemperature()))
                .projectList(projectHistory)
                .build();
    }
}