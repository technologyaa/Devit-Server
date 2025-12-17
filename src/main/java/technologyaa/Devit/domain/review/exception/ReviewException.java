package technologyaa.Devit.domain.review.exception;

public class ReviewException extends RuntimeException {
    private final ReviewErrorCode reviewErrorCode;

    public ReviewException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode.getMessage());
        this.reviewErrorCode = reviewErrorCode;
    }

    public ReviewException(ReviewErrorCode reviewErrorCode, String message) {
        super(message);
        this.reviewErrorCode = reviewErrorCode;
    }
}
