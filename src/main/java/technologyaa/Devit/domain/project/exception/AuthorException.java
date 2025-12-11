package technologyaa.Devit.domain.project.exception;

import lombok.Getter;

@Getter
public class AuthorException extends RuntimeException {
    private final AuthorErrorCode authorErrorCode;

  public AuthorException(AuthorErrorCode authorErrorCode) {
    super(authorErrorCode.getMessage());
    this.authorErrorCode = authorErrorCode;
  }

  public AuthorException(AuthorErrorCode authorErrorCode, String message) {
    super(message);
    this.authorErrorCode = authorErrorCode;
  }
}
