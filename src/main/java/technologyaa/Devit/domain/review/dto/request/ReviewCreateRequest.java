package technologyaa.Devit.domain.review.dto.request;

import lombok.Getter;

@Getter
public class ReviewCreateRequest {
<<<<<<< HEAD
=======
    private Long reviewerId;
>>>>>>> origin/review
    private Long revieweeId;
    private Long projectId;
    private int rating;
    private String comment;
}