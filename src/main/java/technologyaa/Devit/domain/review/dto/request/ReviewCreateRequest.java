package technologyaa.Devit.domain.review.dto.request;

import lombok.Getter;

@Getter
public class ReviewCreateRequest {
    private Long reviewerId;
    private Long revieweeId;
    private Long projectId;
    private int rating;
    private String comment;
}