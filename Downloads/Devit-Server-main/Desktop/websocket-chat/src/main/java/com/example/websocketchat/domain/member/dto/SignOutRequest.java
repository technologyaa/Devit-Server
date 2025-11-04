package com.example.websocketchat.domain.member.dto;

import jakarta.validation.constraints.NotNull;

public record SignOutRequest(
        @NotNull
        String username
) {
}

