package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.Station;
import subway.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class LineService {

  private final LineRepository lineRepository;

  private final StationRepository stationRepository;

  public LineService(LineRepository lineRepository, StationRepository stationRepository) {
    this.lineRepository = lineRepository;
    this.stationRepository = stationRepository;
  }

  @Transactional
  public void createLine(LineCreateRequest lineCreateRequest){
    Station upStation = stationRepository.findById(lineCreateRequest.getUpStationId()).orElseThrow(RuntimeException::new);
    Station downStation = stationRepository.findById(lineCreateRequest.getDownStationId()).orElseThrow(RuntimeException::new);
    lineRepository.save(lineCreateRequest.toLine(upStation, downStation));
  }

  public List<LineResponse> getLines(){
    return lineRepository.findAllJoin().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
  }

  public LineResponse getLine(Long id){
    return LineResponse.from(lineRepository.findById(id).orElseThrow(RuntimeException::new));
  }

  @Transactional
  public LineResponse modifyLine(Long id, LineModifyRequest lineModifyRequest){
    Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
    line.updateName(lineModifyRequest.getName());
    line.updateColor(lineModifyRequest.getColor());
    return LineResponse.from(line);
  }
}
