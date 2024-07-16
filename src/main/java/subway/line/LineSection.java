package subway.line;

import subway.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class LineSection {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne
  @JoinColumn(name = "line_id")
  Line line;

  Long index;

  @OneToOne
  Station upStationId;

  @OneToOne
  Station downStationId;

  int distance;

  public LineSection() {
  }

  public LineSection(Long id, Line line, Long index, Station upStation, Station downStation, int distance) {
    this.id = id;
    this.line = line;
    this.index = index;
    this.upStationId = upStation;
    this.downStationId = downStation;
    this.distance = distance;
  }

  public Long getId() {
    return id;
  }

  public Line getLine() {
    return line;
  }

  public Long getIndex() {
    return index;
  }

  public Station getUpStationId() {
    return upStationId;
  }

  public Station getDownStationId() {
    return downStationId;
  }

  public int getDistance() {
    return distance;
  }
}
