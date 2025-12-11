package technologyaa.Devit.domain.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectUpdateRequest {
    @NotNull
    private String title;

    private String content;

    @NotNull
    private Boolean isCompleted;
}
