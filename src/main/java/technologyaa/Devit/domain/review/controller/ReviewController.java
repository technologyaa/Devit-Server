package technologyaa.Devit.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.common.APIResponse;
import technologyaa.Devit.domain.review.dto.request.SendReviewRequest;
import technologyaa.Devit.domain.review.dto.request.UpdateReviewRequest;
import technologyaa.Devit.domain.review.dto.response.ReadReviewResponse;
import technologyaa.Devit.domain.review.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "프로젝트에 참여한 멤버에게 리뷰를 작성합니다.")
    @PostMapping("/projects/{projectId}/members/{memberId}")
    public ResponseEntity<APIResponse<String>> sendReview(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @Valid @RequestBody SendReviewRequest request) {
        APIResponse<String> response = reviewService.sendReview(request, projectId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내가 보낸 리뷰 조회", description = "내가 다른 사람에게 작성한 리뷰 목록을 조회합니다.")
    @GetMapping("/sent")
    public ResponseEntity<APIResponse<List<ReadReviewResponse>>> getMySentReviews() {
        APIResponse<List<ReadReviewResponse>> response = reviewService.MySentReviewFinder();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 받은 리뷰 조회", description = "다른 사람이 나에게 작성한 리뷰 목록을 조회합니다.")
    @GetMapping("/received")
    public ResponseEntity<APIResponse<List<ReadReviewResponse>>> getReceivedReviews() {
        APIResponse<List<ReadReviewResponse>> response = reviewService.ReceivedReviewQueryService();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 수정", description = "내가 작성한 리뷰를 수정합니다.")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<APIResponse<String>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        APIResponse<String> response = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 삭제", description = "내가 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<APIResponse<String>> deleteReview(@PathVariable Long reviewId) {
        APIResponse<String> response = reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(response);
    }
}