package technologyaa.Devit.domain.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.oauth.entity.User;
import technologyaa.Devit.domain.auth.oauth.repository.UserRepository;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.review.dto.request.ReviewCreateRequest;
import technologyaa.Devit.domain.review.dto.response.AverageRatingResponse;
import technologyaa.Devit.domain.review.dto.response.ReviewResponse;
import technologyaa.Devit.domain.review.entity.Review;
import technologyaa.Devit.domain.auth.jwt.exception.ReviewErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.ReviewException;
import technologyaa.Devit.domain.review.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {

        User reviewer = getUserOrThrow(request.getReviewerId());
        User reviewee = getUserOrThrow(request.getRevieweeId());

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() ->
                        new ReviewException(ReviewErrorCode.PROJECT_NOT_FOUND));

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

    @Transactional
    public AverageRatingResponse getAverageRating(Long userId) {

        User user = getUserOrThrow(userId);

        List<Review> reviews = reviewRepository.findByReviewee(user);

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        double roundedAverage = Math.round(average * 10.0) / 10.0;

        return AverageRatingResponse.builder()
                .userId(userId)
                .averageRating(roundedAverage)
                .build();
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ReviewException(ReviewErrorCode.USER_NOT_FOUND));
    }

    private void validateReview(User reviewer, User reviewee, Project project, int rating) {

        if (rating < 0 || rating > 5) {
            throw new ReviewException(ReviewErrorCode.INVALID_RATING);
        }

        if (!project.getParticipants().contains(reviewer)
                || !project.getParticipants().contains(reviewee)) {
            throw new ReviewException(ReviewErrorCode.FORBIDDEN_REVIEW);
        }

        if (reviewRepository.existsByReviewerAndRevieweeAndProject(
                reviewer, reviewee, project)) {
            throw new ReviewException(ReviewErrorCode.DUPLICATE_REVIEW);
        }
    }
}