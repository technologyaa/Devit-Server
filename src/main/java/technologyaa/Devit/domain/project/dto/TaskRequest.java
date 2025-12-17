package technologyaa.Devit.domain.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technologyaa.Devit.domain.project.entity.Task;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "업무 생성/수정 요청 DTO")
public class TaskRequest {
    @Schema(description = "업무 제목", example = "새로운 업무")
    private String title;
    
    @Schema(description = "업무 설명", example = "업무에 대한 상세 설명")
    private String description;
    
    @Schema(description = "업무 상태", example = "TODO", allowableValues = {"TODO", "IN_PROGRESS", "DONE"})
    private Task.TaskStatus status;
}

