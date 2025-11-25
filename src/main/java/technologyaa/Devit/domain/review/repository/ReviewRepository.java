package technologyaa.Devit.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.auth.oauth.entity.User;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.review.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReviewerAndRevieweeAndProject(User reviewer, User reviewee, Project project);

    List<Review> findByReviewee(User reviewee);
}