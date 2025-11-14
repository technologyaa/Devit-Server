package com.example.websocketchat.domain.project.dto;

import com.example.websocketchat.domain.project.entity.Major;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProjectDetailResponse(
        Long id,
        String title,
        String description,
        Major major,
        String ownerName,
        String thumbnailUrl,
        long creditBudget,
        LocalDateTime createdAt,
        List<ProjectTaskResponse> tasks
) {
}

