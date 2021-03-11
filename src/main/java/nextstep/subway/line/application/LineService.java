package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.LineNameDuplicatedException;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;

    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        if (lineRepository.existsByName(request.getName())) {
            throw new LineNameDuplicatedException();
        }

        return LineResponse.of(createLine(request));
    }

    private Line createLine(LineRequest request) {
        Line line = request.toLine();
        addSection(line, request.getUpStationId(), request.getDownStationId(), request.getDistance());

        return lineRepository.save(line);
    }

    private void addSection(Line line, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findStationById(upStationId);
        Station downStation = stationService.findStationById(downStationId);

        line.addSection(upStation, downStation, distance);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long id) {
        return LineResponse.of(findLine(id));
    }

    @Transactional(readOnly = true)
    public Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
    }

    public LineResponse updateLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        line.update(request.toLine());

        return LineResponse.of(line);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long id, SectionRequest request) {
        Line line = findLine(id);
        addSection(line, request.getUpStationId(), request.getDownStationId(), request.getDistance());

        return LineResponse.of(line);
    }

    public void deleteSection(Long id, Long stationId) {
        Line line = findLine(id);
        Station station = stationService.findStationById(stationId);

        line.deleteSection(station);
    }
}
