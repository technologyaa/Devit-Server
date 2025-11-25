package technologyaa.Devit.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.review.dto.response.AverageRatingResponse;
import technologyaa.Devit.domain.review.dto.request.ReviewCreateRequest;
import technologyaa.Devit.domain.review.dto.response.ReviewResponse;
import technologyaa.Devit.domain.review.service.ReviewService;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponse createReview(@RequestBody ReviewCreateRequest request) {
        return reviewService.createReview(request);
    }

    @GetMapping("/average/{userId}")
    public AverageRatingResponse getAverageRating(@PathVariable Long userId) {
        return reviewService.getAverageRating(userId);
    }
}