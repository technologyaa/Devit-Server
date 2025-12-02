package technologyaa.Devit.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.project.entity.TaskFile;

import java.util.List;
import java.util.Optional;

public interface TaskFileRepository extends JpaRepository<TaskFile, Long> {
    List<TaskFile> findByTask_TaskId(Long taskId);
    Optional<TaskFile> findByTask_TaskIdAndFileId(Long taskId, Long fileId);
    void deleteByTask_TaskId(Long taskId);
}


