package com.example.websocketchat.domain.project.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProjectSummaryResponse(
        Long id,
        String title,
        String description,
        String ownerName,
        String thumbnailUrl,
        long creditBudget,
        LocalDateTime createdAt
) {
}

