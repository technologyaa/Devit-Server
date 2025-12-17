package technologyaa.Devit.domain.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${spring.file.upload.profile-dir}")
    private String profileDir;
    @Value("${spring.file.upload.project-dir}")
    private String projectDir;

    public String storeProfileFile(MultipartFile file) throws IOException {
        return storeFile(file, profileDir);
    }

    public String storeProjectFile(MultipartFile file) throws IOException {
        return storeFile(file, projectDir);
    }

    public String storeFile(MultipartFile file, String uploadDir) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        if(originalFilename.contains("..")) {
            throw new IOException("파일명에 잘못된 경로가 포함되어 있습니다.");
        }

        String fileExtension = "";
        if(originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        Path targetLocation = uploadPath.resolve(uniqueFilename);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return uploadDir + "/" + uniqueFilename;
    }

    public boolean deleteFile(String filename) {
        try {
            if(filename != null && !filename.isEmpty()) {
                Path path = Paths.get(filename);
                return Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            System.out.println("파일 삭제 실패");
            return false;
    }
        return false;
    }

    public String getProfileUploadDir() {
        return profileDir;
    }

    public String getProjectUploadDir() {
        return projectDir;
    }
}