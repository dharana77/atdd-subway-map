package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;

import java.time.LocalDateTime;
import java.util.List;

public class LineReadResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<String> stations;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public LineReadResponse(final Line line, final List<String> stations) {
        this(line.getId(), line.getName(), line.getColor(), stations, line.getCreatedDate(), line.getModifiedDate());
    }

    public LineReadResponse(final Long id, final String name, final String color, final List<String> stations,
                            final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
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

    public List<String> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}