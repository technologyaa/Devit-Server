package technologyaa.Devit.domain.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import technologyaa.Devit.domain.project.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p JOIN FETCH p.author JOIN p.members m WHERE m.id = :memberId")
    List<Project> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT p FROM Project p JOIN FETCH p.author")
    List<Project> findAllWithAuthor();

    @Query("SELECT p FROM Project p JOIN FETCH p.author WHERE p.projectId = :id")
    Optional<Project> findByIdWithAuthor(@Param("id") Long id);
}

