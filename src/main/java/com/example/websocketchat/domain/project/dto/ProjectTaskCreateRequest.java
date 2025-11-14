package com.example.websocketchat.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectTaskCreateRequest(
        @NotBlank
        @Size(max = 80)
        String title,

        @Size(max = 4000)
        String description,

        Long creditReward
) {
}

