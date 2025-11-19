package technologyaa.Devit.domain.profile.dto;

import lombok.Builder;

@Builder
public record ProfileProjectResponse(
        String name,
        String points
) { }