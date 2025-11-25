package technologyaa.Devit.domain.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.oauth.entity.User;
import technologyaa.Devit.domain.auth.oauth.repository.UserRepository;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.review.dto.response.AverageRatingResponse;
import technologyaa.Devit.domain.review.dto.request.ReviewCreateRequest;
import technologyaa.Devit.domain.review.dto.response.ReviewResponse;
import technologyaa.Devit.domain.review.entity.Review;
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

        User reviewer = getUserOrThrow(request.getReviewerId(), "Reviewer not found");
        User reviewee = getUserOrThrow(request.getRevieweeId(), "Reviewee not found");
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalStateException("Project not found"));

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
        User user = getUserOrThrow(userId, "User not found");

        List<Review> reviews = reviewRepository.findByReviewee(user);

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return AverageRatingResponse.builder()
                .userId(userId)
                .averageRating(average)
                .build();
    }

    private User getUserOrThrow(Long id, String message) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(message));
    }

    private void validateReview(User reviewer, User reviewee, Project project, int rating) {
        if (rating < 0 || rating > 100) {
            throw new IllegalArgumentException("Rating must be between 0 and 100");
        }

        if (!project.getParticipants().contains(reviewer)) {
            throw new IllegalStateException("Reviewer is not a participant of the project");
        }

        if (!project.getParticipants().contains(reviewee)) {
            throw new IllegalStateException("Reviewee is not a participant of the project");
        }

        if (reviewRepository.existsByReviewerAndRevieweeAndProject(reviewer, reviewee, project)) {
            throw new IllegalStateException("Already reviewed this user for this project.");
        }
    }
}