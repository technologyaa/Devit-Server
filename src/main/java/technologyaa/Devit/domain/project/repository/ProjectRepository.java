package technologyaa.Devit.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}

