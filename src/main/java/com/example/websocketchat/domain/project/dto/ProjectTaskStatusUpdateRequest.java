package com.example.websocketchat.domain.project.dto;

import jakarta.validation.constraints.NotNull;

public record ProjectTaskStatusUpdateRequest(
        @NotNull
        Boolean done
) {
}

