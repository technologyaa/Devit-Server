package com.example.websocketchat.domain.project.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProjectTaskResponse(
        Long id,
        String title,
        String description,
        boolean isDone,
        int sortOrder,
        long creditReward,
        LocalDateTime createdAt
) {
}

