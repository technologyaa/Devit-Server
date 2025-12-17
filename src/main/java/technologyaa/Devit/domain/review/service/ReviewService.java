package technologyaa.Devit.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.exception.AuthErrorCode;
import technologyaa.Devit.domain.auth.jwt.exception.AuthException;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.developer.entity.Developer;
import technologyaa.Devit.domain.developer.repository.DeveloperRepository;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.project.exception.ProjectErrorCode;
import technologyaa.Devit.domain.project.exception.ProjectException;
import technologyaa.Devit.domain.project.repository.ProjectRepository;
import technologyaa.Devit.domain.project.service.ProjectService;
import technologyaa.Devit.domain.review.dto.request.SendReviewRequest;
import technologyaa.Devit.domain.review.dto.request.UpdateReviewRequest;
import technologyaa.Devit.domain.review.dto.response.ReadReviewResponse;
import technologyaa.Devit.domain.review.entity.Review;
import technologyaa.Devit.domain.review.exception.ReviewErrorCode;
import technologyaa.Devit.domain.review.exception.ReviewException;
import technologyaa.Devit.domain.review.repository.ReviewRepository;
import technologyaa.Devit.global.util.SecurityUtil;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProjectService projectService;
    private final SecurityUtil securityUtil;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final DeveloperRepository developerRepository;

    public APIResponse<String> sendReview(SendReviewRequest request, Long projectId, Long memberId) {
        Member sender = securityUtil.getMember();
        List<Member> members = projectService.getProjectMembers(projectId);
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));

        boolean isMemberInProject = members.stream()
                .anyMatch(member -> member.getId().equals(memberId));

        if (!isMemberInProject) {
            throw new ReviewException(ReviewErrorCode.MEMBER_NOT_IN_PROJECT);
        }

        try {
            reviewRepository.save(Review.builder()
                    .rating(request.rating())
                    .content(request.content())
                    .sender(sender)
                    .receiver(receiver)
                    .project(project)
                    .build());
        } catch (DataIntegrityViolationException e) {
            throw new ReviewException(ReviewErrorCode.INVALID_INPUT_VALUE);
        }

        Developer developer = developerRepository.findById(receiver.getId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        developer.setTemperature(developer.getTemperature().subtract(BigDecimal.valueOf(3)));
        developerRepository.save(developer);

        return APIResponse.ok("리뷰가 작성되었습니다.");
    }

    public APIResponse<List<ReadReviewResponse>> MySentReviewFinder() {
        Member member = securityUtil.getMember();
        List<ReadReviewResponse> reviews = reviewRepository.findAllBySender(member)
                .stream()
                .map(ReadReviewResponse::form)
                .toList();

        return APIResponse.ok(reviews);
    }

    public APIResponse<List<ReadReviewResponse>> ReceivedReviewQueryService() {
        Member member = securityUtil.getMember();
        List<ReadReviewResponse> reviews = reviewRepository.findAllByReceiver(member)
                .stream()
                .map(ReadReviewResponse::form)
                .toList();

        return APIResponse.ok(reviews);
    }

    public APIResponse<String> updateReview(Long reviewId, UpdateReviewRequest request) {
        Member member = securityUtil.getMember();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!review.getSender().getId().equals(member.getId())) {
            throw new ReviewException(ReviewErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }

        review.update(request.rating(), request.content());
        reviewRepository.save(review);

        return APIResponse.ok("리뷰가 수정되었습니다.");
    }

    public APIResponse<String> deleteReview(Long reviewId) {
        Member member = securityUtil.getMember();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!review.getSender().getId().equals(member.getId())) {
            throw new ReviewException(ReviewErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }

        reviewRepository.delete(review);

        return APIResponse.ok("리뷰가 삭제되었습니다.");
    }
}
