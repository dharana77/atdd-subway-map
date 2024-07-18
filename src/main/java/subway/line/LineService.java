package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.Station;
import subway.StationRepository;
import subway.exceptions.errors.SubwayException;
import subway.line.dto.LineCreateRequest;
import subway.line.dto.LineModifyRequest;
import subway.line.dto.LineResponse;
import subway.line.dto.LineSectionAppendRequest;
import subway.line.dto.StationsAtLine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static subway.exceptions.errors.SubwayErrorCode.BAD_REQUEST;
import static subway.exceptions.errors.SubwayErrorCode.NOT_FOUND;

@Transactional(readOnly = true)
@Service
public class LineService {

  private final LineRepository lineRepository;

  private final StationRepository stationRepository;

  private final LineSectionRepository lineSectionRepository;

  public LineService(LineRepository lineRepository, StationRepository stationRepository, LineSectionRepository lineSectionRepository) {
    this.lineRepository = lineRepository;
    this.stationRepository = stationRepository;
    this.lineSectionRepository = lineSectionRepository;
  }

  @Transactional
  public Line createLine(LineCreateRequest lineCreateRequest) {
    StationsAtLine stationsAtLine = getStationsAtLine(lineCreateRequest.getUpStationId(), lineCreateRequest.getDownStationId());
    return lineRepository.save(lineCreateRequest.toLine(stationsAtLine.getUpStation(), stationsAtLine.getDownStation()));
  }

  public List<LineResponse> getLines() {
    return lineRepository.findAllJoin().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
  }

  public LineResponse getLine(Long id) {
    return LineResponse.from(lineRepository.findById(id).orElseThrow(() -> new SubwayException(NOT_FOUND)));
  }

  @Transactional
  public LineResponse modifyLine(Long id, LineModifyRequest lineModifyRequest) {
    Line line = getLineById(id);
    line.updateName(lineModifyRequest.getName());
    line.updateColor(lineModifyRequest.getColor());
    return LineResponse.from(line);
  }

  @Transactional
  public void deleteLine(Long id){
    lineRepository.deleteById(id);
  }

  private StationsAtLine getStationsAtLine(Long upStationId, Long downStationId) {
    Station upStation = stationRepository.findById(upStationId).orElseThrow(()-> new SubwayException(NOT_FOUND));
    Station downStation = stationRepository.findById(downStationId).orElseThrow(()-> new SubwayException(NOT_FOUND));

    return new StationsAtLine(upStation, downStation);
  }

  private Line getLineById(Long id) {
    return lineRepository.findById(id).orElseThrow(()-> new SubwayException(NOT_FOUND));
  }

  @Transactional
  public void appendLineSection(Long id, LineSectionAppendRequest lineSectionAppendRequest) {
    Line line = getLineById(id);

    List<LineSection> lineSections = getLineSectionsByLineId(id);
    Long index = lineSections.stream().map(item -> item.index).mapToLong(Long::longValue).max().orElse(1L);
    Set<Long> lineSectionStations = new HashSet<>();
    lineSections.stream().forEach(item -> {
      lineSectionStations.add(item.getDownStation().getId());
      lineSectionStations.add(item.getUpStation().getId());
    });

    if (!lineSectionAppendRequest.getUpStationId().equals(index) ||
      lineSectionStations.contains(lineSectionAppendRequest.getDownStationId())) {
      throw new SubwayException(BAD_REQUEST);
    }

    lineSectionRepository.save(
      new LineSection(
        null,
        line,
        index + 1,
        stationRepository.findById(lineSectionAppendRequest.getUpStationId()).orElseThrow(() -> new SubwayException(NOT_FOUND)),
        stationRepository.findById(lineSectionAppendRequest.getDownStationId()).orElseThrow(() -> new SubwayException(NOT_FOUND)),
        lineSectionAppendRequest.getDistance()));
  }

  @Transactional
  public void deleteLineSection(Long lineId, Long stationId) {
    List<LineSection> lineSectiosn = lineSectionRepository.findAllByLineId(lineId);

    LineSection lastLineSection = lineSectiosn.stream().max((a, b) -> (int) (a.index - b.index))
      .orElseThrow(() -> new SubwayException(NOT_FOUND));

    if (lastLineSection.getDownStation().getId() != stationId || lineSectiosn.size() <= 1) {
      throw new SubwayException(BAD_REQUEST);
    }

    lineSectionRepository.deleteByLineIdAndStationId(stationId);
  }

  private List<LineSection> getLineSectionsByLineId(Long lineId) {
    return lineSectionRepository.findAllByLineId(lineId);
  }
}
