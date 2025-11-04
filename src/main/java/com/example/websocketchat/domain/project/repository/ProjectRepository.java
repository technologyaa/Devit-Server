package com.example.websocketchat.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.websocketchat.domain.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}

