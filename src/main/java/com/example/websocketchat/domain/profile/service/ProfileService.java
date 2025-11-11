package com.example.websocketchat.domain.profile.service;

import com.example.websocketchat.domain.developer.entity.DeveloperProfile;
import com.example.websocketchat.domain.developer.repository.DeveloperProfileRepository;
import com.example.websocketchat.domain.profile.dto.ProfileProjectResponse;
import com.example.websocketchat.domain.profile.dto.ProfileResponse;
import com.example.websocketchat.domain.project.entity.Project;
import com.example.websocketchat.domain.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final DeveloperProfileRepository developerProfileRepository;
    private final ProjectRepository projectRepository;

    @Transactional(Transactional.TxType.SUPPORTS)
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

