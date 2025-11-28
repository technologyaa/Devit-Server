package technologyaa.Devit.domain.developer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.developer.entity.Developer;

public interface DeveloperRepository extends JpaRepository<Developer,Long> {
}
