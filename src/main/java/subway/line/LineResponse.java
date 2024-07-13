package subway.line;

import subway.Station;

import java.util.List;

public class LineResponse {

  private Long id;

  private String name;

  private String color;

  private List<Station> stations;

  public LineResponse() {
  }

  public LineResponse(Long id, String name, String color, List<Station> stations) {
    this.id = id;
    this.name = name;
    this.color = color;
    this.stations = stations;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  public List<Station> getStations(){
    return stations;
  }

  public static LineResponse from(Line line) {
    return new LineResponse(line.getId(), line.getName(), line.getColor(), List.of(line.getUpStation(), line.getDownStation()));
  }
}
