package com.example.websocketchat.domain.developer.service;

import com.example.websocketchat.domain.developer.dto.DeveloperSummaryResponse;
import com.example.websocketchat.domain.developer.entity.DeveloperProfile;
import com.example.websocketchat.domain.developer.repository.DeveloperProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeveloperProfileService {

    private final DeveloperProfileRepository developerProfileRepository;

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DeveloperSummaryResponse> getRecommendedDevelopers() {
        return developerProfileRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    private DeveloperSummaryResponse toSummary(DeveloperProfile profile) {
        return DeveloperSummaryResponse.builder()
                .name(profile.getName())
                .job(profile.getJob())
                .summary(profile.getSummary())
                .profileImageUrl(profile.getProfileImageUrl())
                .build();
    }
}

