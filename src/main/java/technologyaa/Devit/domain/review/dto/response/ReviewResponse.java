package technologyaa.Devit.domain.review.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private Long reviewerId;
    private Long revieweeId;
    private Long projectId;
    private int rating;
    private String comment;
    private String createdAt;
}