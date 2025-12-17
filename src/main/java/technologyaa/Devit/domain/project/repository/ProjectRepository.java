package technologyaa.Devit.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import technologyaa.Devit.domain.project.entity.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.id = :memberId")
    List<Project> findByMemberId(@Param("memberId") Long memberId);
}

