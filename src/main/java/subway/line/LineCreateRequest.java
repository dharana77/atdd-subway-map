package subway.line;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LineCreateRequest {

  private String name;
  private String color;
  private Long upStationId;
  private Long downStationId;
  private int distance;

  public LineCreateRequest(){
  }

  public LineCreateRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
    this.name = name;
    this.color = color;
    this.upStationId = upStationId;
    this.downStationId = downStationId;
    this.distance = distance;
  }

  public Line toLine() {
    return new Line(null, name, upStationId, downStationId, distance);
  }
}
