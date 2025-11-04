package com.example.websocketchat.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import com.example.websocketchat.domain.member.entity.Role;

public record SignUpRequest(
        @NotNull
        String username,
        @NotNull
        String password,
        @NotNull
        String email,
        Role role
)
{ }

