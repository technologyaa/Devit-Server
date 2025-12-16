package technologyaa.Devit.domain.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.jwt.exception.ReviewErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.ReviewException;
import technologyaa.Devit.domain.auth.oauth.entity.User;
import technologyaa.Devit.domain.auth.oauth.repository.UserRepository;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.review.dto.request.ReviewCreateRequest;
import technologyaa.Devit.domain.review.dto.response.AverageRatingResponse;
import technologyaa.Devit.domain.review.dto.response.ReviewResponse;
import technologyaa.Devit.domain.review.entity.Review;
import technologyaa.Devit.domain.review.repository.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    /** 리뷰 생성 */
    public ReviewResponse createReview(ReviewCreateRequest request) {
        User reviewer = getCurrentUser();
        User reviewee = getUserOrThrow(request.getRevieweeId());

        if (reviewer.equals(reviewee)) {
            throw new ReviewException(ReviewErrorCode.INVALID_RATING);
        }

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.PROJECT_NOT_FOUND));

        validateReview(reviewer, reviewee, project, request.getRating());

        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .project(project)
                .rating(request.getRating())
                .comment(request.getComment())
                .build(); // createdAt는 @PrePersist로 자동 설정

        Review saved = reviewRepository.save(review);

        return ReviewResponse.builder()
                .reviewId(saved.getId())
                .reviewerId(reviewer.getId())
                .revieweeId(reviewee.getId())
                .projectId(project.getProjectId())
                .rating(saved.getRating())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt().toString())
                .build();
    }

    /** 특정 유저 평균 평점 조회 */
    public AverageRatingResponse getAverageRating(Long userId) {
        User user = getUserOrThrow(userId);
        List<Review> reviews = reviewRepository.findByReviewee(user);

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return AverageRatingResponse.builder()
                .userId(userId)
                .averageRating(Math.round(avg * 10.0) / 10.0)
                .build();
    }

    /** 현재 로그인 사용자 조회 */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ReviewException(ReviewErrorCode.FORBIDDEN_REVIEW);
        }
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.USER_NOT_FOUND));
    }

    /** ID로 사용자 조회 */
    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.USER_NOT_FOUND));
    }

    /** 리뷰 유효성 검증 (평점, 참여 여부, 중복) */
    private void validateReview(User reviewer, User reviewee, Project project, int rating) {
        if (rating < 0 || rating > 5) {
            throw new ReviewException(ReviewErrorCode.INVALID_RATING);
        }

        if (!project.getParticipants().contains(reviewer) || !project.getParticipants().contains(reviewee)) {
            throw new ReviewException(ReviewErrorCode.FORBIDDEN_REVIEW);
        }

        if (reviewRepository.existsByReviewerAndRevieweeAndProject(reviewer, reviewee, project)) {
            throw new ReviewException(ReviewErrorCode.DUPLICATE_REVIEW);
        }
    }
}