package com.example.websocketchat.domain.home.dto;

import lombok.Builder;

@Builder
public record QuickLinkResponse(
        String url,
        String logo,
        String alt,
        String text,
        String name,
        String gradient
) {
}

