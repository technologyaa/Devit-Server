package technologyaa.devit.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.devit.domain.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
