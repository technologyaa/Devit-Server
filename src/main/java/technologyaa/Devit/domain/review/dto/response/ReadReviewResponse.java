package technologyaa.Devit.domain.review.dto.response;

import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.project.entity.Project;
import technologyaa.Devit.domain.review.entity.Review;

public record ReadReviewResponse(
        Long id,
        byte rating,
        String content,
        Member sender,
        Member receiver,
        Project project
) {
    public static ReadReviewResponse form(Review review) {
        return new ReadReviewResponse(
                review.getId(),
                review.getRating(),
                review.getContent(),
                review.getSender(),
                review.getReceiver(),
                review.getProject()
        );
    }
}
