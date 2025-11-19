package technologyaa.Devit.domain.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record ProfileResponse(
        @JsonProperty("id")
        String name,
        String job,
        String email,
        @JsonProperty("img")
        String profileImageUrl,
        @JsonProperty("CompletedProjects")
        String completedProjects,
        @JsonProperty("Temp")
        String temperature,
        @JsonProperty("projectList")
        List<ProfileProjectResponse> projectList
) { }