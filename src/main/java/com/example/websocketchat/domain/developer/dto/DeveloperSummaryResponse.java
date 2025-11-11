package com.example.websocketchat.domain.developer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DeveloperSummaryResponse(
        String name,
        String job,
        @JsonProperty("text")
        String summary,
        @JsonProperty("profileImage")
        String profileImageUrl
) {
}

