package com.example.websocketchat.domain.project.repository;

import com.example.websocketchat.domain.project.entity.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> findAllByProject_ProjectIdOrderBySortOrderAscTaskIdAsc(Long projectId);
}

