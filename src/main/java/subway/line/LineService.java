package subway.line;

import org.springframework.stereotype.Service;

@Service
public class LineService {

  private final LineRepository lineRepository;

  public LineService(LineRepository lineRepository) {
    this.lineRepository = lineRepository;
  }

  public void createLine(LineCreateRequest lineCreateRequest){
    lineRepository.save(lineCreateRequest.toLine());
  }
}
