package subway.exceptions.errors;

import lombok.Getter;

@Getter
public class SubwayErrorResponse {
  private SubwayErrorCode status;
  private String statusMessage;

  public SubwayErrorResponse() {
  }

  public SubwayErrorResponse(SubwayErrorCode errorCode, String message) {
    this.status = errorCode;
    this.statusMessage = message;
  }
}
