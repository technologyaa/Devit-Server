package technologyaa.Devit.domain.review.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AverageRatingResponse {
    private Long userId;
    private double averageRating;
}