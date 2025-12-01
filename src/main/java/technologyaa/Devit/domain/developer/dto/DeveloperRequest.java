package technologyaa.Devit.domain.developer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import technologyaa.Devit.domain.developer.entity.Major;

@Getter
public class DeveloperRequest {

    private String introduction;

    private Integer career;

    private String githubId;

    @NotNull
    private Major major;

    private String blog;
}
