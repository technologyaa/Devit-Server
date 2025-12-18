package technologyaa.Devit.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.project.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject_ProjectId(Long projectId);
    // 특정 프로젝트의 DONE이 아닌 Task 개수
    long countByProject_ProjectIdAndStatusNot(
            Long projectId,
            Task.TaskStatus status
    );

    // 프로젝트에 속한 전체 Task 개수
    long countByProject_ProjectId(Long projectId);
}


