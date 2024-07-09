package subway.line;

import org.springframework.stereotype.Service;
import subway.Station;
import subway.StationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

  private final LineRepository lineRepository;

  private final StationRepository stationRepository;

  public LineService(LineRepository lineRepository, StationRepository stationRepository) {
    this.lineRepository = lineRepository;
    this.stationRepository = stationRepository;
  }

  public void createLine(LineCreateRequest lineCreateRequest){
    Station upStation = stationRepository.findById(lineCreateRequest.getUpStationId()).orElseThrow(RuntimeException::new);
    Station downStation = stationRepository.findById(lineCreateRequest.getDownStationId()).orElseThrow(RuntimeException::new);
    lineRepository.save(lineCreateRequest.toLine(upStation, downStation));
  }

  public List<LineResponse> getLines(){
    List<Line> lines = lineRepository.findAll();
    List<LineResponse> lineResponses = new ArrayList<>();
    for (Line line : lines) {
      List<Station> stations = new ArrayList<>();
      stations.add(stationRepository.findById(line.getUpStation().getId()).orElseThrow(RuntimeException::new));
      stations.add(stationRepository.findById(line.getDownStation().getId()).orElseThrow(RuntimeException::new));

      lineResponses.add(new LineResponse(null, line.getName(), line.getColor(), stations));
    }
    return lineResponses;
  }
}
