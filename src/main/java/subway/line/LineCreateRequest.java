package subway.line;

import lombok.Getter;
import subway.Station;

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

  public Line toLine(Station upStation, Station downStation) {
    return new Line(null, name, color, upStation, downStation, distance);
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  public Long getUpStationId() {
    return upStationId;
  }

  public Long getDownStationId() {
    return downStationId;
  }

  public int getDistance() {
    return distance;
  }
}
