package technologyaa.Devit.domain.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technologyaa.Devit.domain.project.entity.Major;

@Getter
@Setter
@NoArgsConstructor
public class ProjectRequest {
    private String title;
    private String content;
    private Major major;
}

