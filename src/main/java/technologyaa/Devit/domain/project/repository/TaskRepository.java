package technologyaa.Devit.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.project.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProject_ProjectId(Long projectId);

    boolean existsByProject_ProjectId(Long projectId);
}

