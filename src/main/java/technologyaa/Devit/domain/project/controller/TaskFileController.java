package technologyaa.Devit.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.project.entity.TaskFile;
import technologyaa.Devit.domain.project.service.TaskFileService;

import java.io.IOException;
import java.util.List;

@Tag(name = "업무 파일 (Task File)", description = "업무 파일 업로드/다운로드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks/{taskId}/files")
public class TaskFileController {
    private final TaskFileService taskFileService;

    @Operation(summary = "업무 파일 업로드", description = "특정 업무에 파일을 업로드합니다.")
    @PostMapping
    public ResponseEntity<String> uploadFile(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) {
        try {
            TaskFile taskFile = taskFileService.uploadFile(taskId, file);
            return ResponseEntity.ok("파일이 성공적으로 업로드되었습니다. 파일 ID: " + taskFile.getFileId());
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Operation(summary = "업무 파일 목록 조회", description = "특정 업무의 모든 파일 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<TaskFile>> getFiles(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        List<TaskFile> files = taskFileService.getFilesByTaskId(taskId);
        return ResponseEntity.ok(files);
    }

    @Operation(summary = "업무 파일 다운로드", description = "특정 업무의 파일을 다운로드합니다.")
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long fileId) {
        try {
            Resource resource = taskFileService.downloadFile(taskId, fileId);
            String originalFileName = taskFileService.getOriginalFileName(taskId, fileId);
            String contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + originalFileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "업무 파일 삭제", description = "특정 업무의 파일을 삭제합니다.")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long fileId) {
        try {
            taskFileService.deleteFile(taskId, fileId);
            return ResponseEntity.ok("파일이 성공적으로 삭제되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}


