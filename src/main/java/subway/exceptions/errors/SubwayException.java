package subway.exceptions.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class SubwayException extends RuntimeException {
  private final SubwayErrorCode errorCode;

  public SubwayException(SubwayErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public SubwayErrorCode getErrorCode(){
    return errorCode;
  }

  public String getMessage() {
    return errorCode.getMessage();
  }
}
