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

        // 1. 에러 메시지 한국어 변경
        User reviewer = getUserOrThrow(request.getReviewerId(), "평가자를 찾을 수 없습니다.");
        User reviewee = getUserOrThrow(request.getRevieweeId(), "평가 대상을 찾을 수 없습니다.");
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalStateException("프로젝트를 찾을 수 없습니다."));

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
        User user = getUserOrThrow(userId, "사용자를 찾을 수 없습니다.");

        List<Review> reviews = reviewRepository.findByReviewee(user);

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        // 소수점 첫째 자리까지 반올림
        double roundedAverage = Math.round(average * 10.0) / 10.0;

        return AverageRatingResponse.builder()
                .userId(userId)
                .averageRating(roundedAverage)
                .build();
    }

    private User getUserOrThrow(Long id, String message) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(message));
    }

    private void validateReview(User reviewer, User reviewee, Project project, int rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("평점은 0점에서 5점 사이여야 합니다.");
        }

        if (!project.getParticipants().contains(reviewer)) {
            throw new IllegalStateException("프로젝트 참여자가 아닙니다.");
        }

        if (!project.getParticipants().contains(reviewee)) {
            throw new IllegalStateException("프로젝트 참여자가 아닙니다.");
        }

        if (reviewRepository.existsByReviewerAndRevieweeAndProject(reviewer, reviewee, project)) {
            throw new IllegalStateException("해당 프로젝트에 대해 이미 평가한 사용자입니다.");
        }
    }
}