
package technologyaa.Devit.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import technologyaa.Devit.domain.project.entity.Task;
import technologyaa.Devit.domain.project.entity.TaskFile;
import technologyaa.Devit.domain.project.repository.TaskFileRepository;
import technologyaa.Devit.domain.project.repository.TaskRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskFileService {
    private final TaskFileRepository taskFileRepository;
    private final TaskRepository taskRepository;

    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    public TaskFile uploadFile(Long taskId, MultipartFile file) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("해당 업무를 찾을 수 없습니다."));

        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일명 생성 (UUID + 원본 파일명)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("파일명이 없습니다.");
        }

        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // 파일 저장
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // TaskFile 엔티티 생성 및 저장
        TaskFile taskFile = TaskFile.builder()
                .task(task)
                .fileName(fileName)
                .originalFileName(originalFilename)
                .fileSize(file.getSize())
                .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .build();

        return taskFileRepository.save(taskFile);
    }

    public Resource downloadFile(Long taskId, Long fileId) throws IOException {
        TaskFile taskFile = taskFileRepository
                .findByTask_TaskIdAndFileId(taskId, fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        Path filePath = Paths.get(uploadDir).resolve(taskFile.getFileName());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("파일을 읽을 수 없습니다: " + taskFile.getFileName());
        }

        return resource;
    }

    public List<TaskFile> getFilesByTaskId(Long taskId) {
        return taskFileRepository.findByTask_TaskId(taskId);
    }

    public void deleteFile(Long taskId, Long fileId) throws IOException {
        TaskFile taskFile = taskFileRepository
                .findByTask_TaskIdAndFileId(taskId, fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        // 파일 시스템에서 파일 삭제
        Path filePath = Paths.get(uploadDir).resolve(taskFile.getFileName());
        Files.deleteIfExists(filePath);

        // 데이터베이스에서 삭제
        taskFileRepository.delete(taskFile);
    }

    public String getOriginalFileName(Long taskId, Long fileId) {
        TaskFile taskFile = taskFileRepository
                .findByTask_TaskIdAndFileId(taskId, fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
        return taskFile.getOriginalFileName();
    }
}

