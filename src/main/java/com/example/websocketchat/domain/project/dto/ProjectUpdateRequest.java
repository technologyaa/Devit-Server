package com.example.websocketchat.domain.project.dto;

import com.example.websocketchat.domain.project.entity.Major;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectUpdateRequest(
        @NotBlank
        @Size(max = 80)
        String title,
        @Size(max = 4000)
        String description,
        @NotBlank
        @Size(max = 60)
        String ownerName,
        String thumbnailUrl,
        @NotNull
        Major major,
        Long creditBudget
) {
}

