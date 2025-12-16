package technologyaa.Devit.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD
import technologyaa.Devit.domain.review.dto.request.ReviewCreateRequest;
import technologyaa.Devit.domain.review.dto.response.AverageRatingResponse;
=======
import technologyaa.Devit.domain.review.dto.response.AverageRatingResponse;
import technologyaa.Devit.domain.review.dto.request.ReviewCreateRequest;
>>>>>>> origin/review
import technologyaa.Devit.domain.review.dto.response.ReviewResponse;
import technologyaa.Devit.domain.review.service.ReviewService;

@RestController
<<<<<<< HEAD
@RequiredArgsConstructor
@RequestMapping("/reviews")
=======
@RequestMapping("/reviews")
@RequiredArgsConstructor
>>>>>>> origin/review
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