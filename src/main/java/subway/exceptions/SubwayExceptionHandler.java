package subway.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exceptions.errors.SubwayErrorResponse;
import subway.exceptions.errors.SubwayException;

@ControllerAdvice
public class SubwayExceptionHandler {

  @ExceptionHandler(SubwayException.class)
  public SubwayErrorResponse handleSubwayException(SubwayException error) {
    return new SubwayErrorResponse(error.getErrorCode(), error.getMessage());
  }
}
