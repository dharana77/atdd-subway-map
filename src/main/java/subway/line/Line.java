package subway.line;

import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Entity
public class Line {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private Long upStationId;

  private Long downStationId;

  private int distance;

  public Line() {
  }

  public Line(Long id, String name, Long upStationId, Long downStationId, int distance) {
    this.id = id;
    this.name = name;
    this.upStationId = upStationId;
    this.downStationId = downStationId;
    this.distance = distance;
  }
}
