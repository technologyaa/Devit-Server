package technologyaa.Devit.domain.developer.dto;

import lombok.Getter;
import technologyaa.Devit.domain.developer.entity.Major;

import java.math.BigDecimal;

@Getter
public class DeveloperResponse {
    private Long memberId;

    private String introduction;

    private Integer career;

    private String githubId;

    private Major major;

    private String blog;

    private BigDecimal temperature;
}
