package technologyaa.Devit.domain.developer.repository;

import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.developer.entity.Developer;
import technologyaa.Devit.domain.developer.entity.Major;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface DeveloperRepository extends JpaRepository<Developer,Long> {
    List<Developer> findByMajor(Major major);

    List<Developer> findByCareerGreaterThanEqual(Integer career);

    List<Developer> findByTemperatureGreaterThanEqual(BigDecimal temperature);
}
