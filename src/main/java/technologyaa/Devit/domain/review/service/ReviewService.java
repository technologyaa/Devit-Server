package technologyaa.Devit.domain.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.jwt.exception.ReviewErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.ReviewException;
=======
import org.springframework.stereotype.Service;
>>>>>>> origin/review
import technologyaa.Devit.domain.auth.oauth.entity.User;
import technologyaa.Devit.domain.auth.oauth.repository.UserRepository;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.review.dto.request.ReviewCreateRequest;
import technologyaa.Devit.domain.review.dto.response.AverageRatingResponse;
import technologyaa.Devit.domain.review.dto.response.ReviewResponse;
import technologyaa.Devit.domain.review.entity.Review;
<<<<<<< HEAD
=======
import technologyaa.Devit.domain.auth.jwt.exception.ReviewErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.ReviewException;
>>>>>>> origin/review
import technologyaa.Devit.domain.review.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
<<<<<<< HEAD
@Transactional
=======
>>>>>>> origin/review
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

<<<<<<< HEAD
    /** 리뷰 생성 */
    public ReviewResponse createReview(ReviewCreateRequest request) {

        User reviewer = getCurrentUser();
        User reviewee = getUserOrThrow(request.getRevieweeId());

        if (reviewer.equals(reviewee)) {
            throw new ReviewException(ReviewErrorCode.INVALID_RATING);
        }

=======
    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {

        User reviewer = getUserOrThrow(request.getReviewerId());
        User reviewee = getUserOrThrow(request.getRevieweeId());

>>>>>>> origin/review
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() ->
                        new ReviewException(ReviewErrorCode.PROJECT_NOT_FOUND));

<<<<<<< HEAD
        // 참여 여부 포함 검증
=======
>>>>>>> origin/review
        validateReview(reviewer, reviewee, project, request.getRating());

        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .project(project)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

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

<<<<<<< HEAD
    /** 특정 유저의 평균 평점 조회 */
=======
>>>>>>> origin/review
    @Transactional
    public AverageRatingResponse getAverageRating(Long userId) {

        User user = getUserOrThrow(userId);

        List<Review> reviews = reviewRepository.findByReviewee(user);

<<<<<<< HEAD
        double avg = reviews.stream()
=======
        double average = reviews.stream()
>>>>>>> origin/review
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

<<<<<<< HEAD
        return AverageRatingResponse.builder()
                .userId(userId)
                .averageRating(Math.round(avg * 10.0) / 10.0)
                .build();
    }

    /** 현재 로그인한 사용자 조회 */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new ReviewException(ReviewErrorCode.FORBIDDEN_REVIEW);
        }

        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() ->
                        new ReviewException(ReviewErrorCode.USER_NOT_FOUND));
    }

    /** ID로 사용자 조회 */
=======
        double roundedAverage = Math.round(average * 10.0) / 10.0;

        return AverageRatingResponse.builder()
                .userId(userId)
                .averageRating(roundedAverage)
                .build();
    }

>>>>>>> origin/review
    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ReviewException(ReviewErrorCode.USER_NOT_FOUND));
    }

<<<<<<< HEAD
    /** 리뷰 유효성 검증 (평점, 참여 여부, 중복) */
    private void validateReview(User reviewer, User reviewee, Project project, int rating) {

        // 평점 체크
=======
    private void validateReview(User reviewer, User reviewee, Project project, int rating) {

>>>>>>> origin/review
        if (rating < 0 || rating > 5) {
            throw new ReviewException(ReviewErrorCode.INVALID_RATING);
        }

<<<<<<< HEAD
        // 프로젝트 참여 여부 확인
=======
>>>>>>> origin/review
        if (!project.getParticipants().contains(reviewer)
                || !project.getParticipants().contains(reviewee)) {
            throw new ReviewException(ReviewErrorCode.FORBIDDEN_REVIEW);
        }

<<<<<<< HEAD
        // 중복 리뷰 체크
        if (reviewRepository.existsByReviewerAndRevieweeAndProject(reviewer, reviewee, project)) {
=======
        if (reviewRepository.existsByReviewerAndRevieweeAndProject(
                reviewer, reviewee, project)) {
>>>>>>> origin/review
            throw new ReviewException(ReviewErrorCode.DUPLICATE_REVIEW);
        }
    }
}