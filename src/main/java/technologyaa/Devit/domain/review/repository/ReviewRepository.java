package technologyaa.Devit.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.auth.oauth.entity.User;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.review.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

<<<<<<< HEAD
    // 중복 리뷰 방지
    boolean existsByReviewerAndRevieweeAndProject(
            User reviewer,
            User reviewee,
            Project project
    );

    // 내가 받은 리뷰
    List<Review> findByReviewee(User reviewee);

    // 내가 쓴 리뷰
    List<Review> findByReviewer(User reviewer);

    // 리뷰 삭제 권한 체크 (작성자 본인)
    boolean existsByIdAndReviewer(Long id, User reviewer);
=======
    boolean existsByReviewerAndRevieweeAndProject(User reviewer, User reviewee, Project project);

    List<Review> findByReviewee(User reviewee);
>>>>>>> origin/review
}