package com.example.websocketchat.global.config;

import com.example.websocketchat.domain.developer.entity.DeveloperProfile;
import com.example.websocketchat.domain.developer.repository.DeveloperProfileRepository;
import com.example.websocketchat.domain.project.entity.Major;
import com.example.websocketchat.domain.project.entity.Project;
import com.example.websocketchat.domain.project.entity.ProjectTask;
import com.example.websocketchat.domain.project.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DevitDataInitializer implements CommandLineRunner {

    private final ProjectRepository projectRepository;
    private final DeveloperProfileRepository developerProfileRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedDevelopers();
        seedProjects();
    }

    private void seedDevelopers() {
        if (developerProfileRepository.count() > 0) {
            return;
        }

        log.info("Seeding developer profiles for demo...");

        developerProfileRepository.save(
                DeveloperProfile.builder()
                        .name("띠용인")
                        .job("iOS")
                        .summary("서비스 Devit의 홈 화면 디자인 작업 경험이 있습니다.")
                        .email("whattv@technologyaa.com")
                        .profileImageUrl("/assets/profile-icon.svg")
                        .temperature(36.5)
                        .completedProjects(2)
                        .build()
        );

        developerProfileRepository.save(
                DeveloperProfile.builder()
                        .name("강장민")
                        .job("게임")
                        .summary("게임 UI/UX 설계 및 구현을 전문으로 합니다.")
                        .email("jangmin@technologyaa.com")
                        .profileImageUrl("/assets/jang.svg")
                        .temperature(42.3)
                        .completedProjects(5)
                        .build()
        );

        developerProfileRepository.save(
                DeveloperProfile.builder()
                        .name("Devit")
                        .job("웹")
                        .summary("Devit 플랫폼 개발을 담당하는 서버 개발자입니다.")
                        .email("devit@technologyaa.com")
                        .profileImageUrl("/assets/dummy-profile.svg")
                        .temperature(33.8)
                        .completedProjects(4)
                        .build()
        );
    }

    private void seedProjects() {
        if (projectRepository.count() > 0) {
            return;
        }

        log.info("Seeding demo projects...");

        Project devitProject = Project.builder()
                .title("Devit")
                .description("개발자와 기획자를 연결하는 Devit 플랫폼입니다.")
                .ownerName("강장민")
                .thumbnailUrl("/assets/dummy-thumbnail.svg")
                .major(Major.WEB)
                .creditBudget(3000L)
                .build();

        devitProject.addTask(ProjectTask.builder()
                .title("서비스 Devit의 홈 화면 디자인")
                .description("홈 대시보드 UI를 디자인하고 사용자 맞춤 카드 컴포넌트를 구현합니다.")
                .sortOrder(0)
                .creditReward(300L)
                .build());

        devitProject.addTask(ProjectTask.builder()
                .title("서비스 Devit의 서버 구축")
                .description("Spring Boot 기반의 REST API와 인증 모듈을 구축합니다.")
                .sortOrder(1)
                .creditReward(2400L)
                .build());

        projectRepository.save(devitProject);
    }
}

