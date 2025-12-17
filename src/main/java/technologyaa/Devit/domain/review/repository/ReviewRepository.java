package technologyaa.Devit.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.review.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllBySender(Member sender);

    List<Review> findAllByReceiver(Member receiver);

    boolean existsBySenderAndReceiverAndProject(Member sender, Member receiver, Project project);
}
