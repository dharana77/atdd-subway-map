package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RequestMapping("/lines")
@RestController
public class LineController {

  private final LineService lineService;

  public LineController(LineService lineService) {
    this.lineService = lineService;
  }

  @PostMapping
  public ResponseEntity createLine(@RequestBody LineCreateRequest lineRequest){
    lineService.createLine(lineRequest);
    return ResponseEntity.created(URI.create("/lines")).build();
  }

  @GetMapping
  public ResponseEntity<List<LineResponse>> getLines(){
    return ResponseEntity.ok().body(lineService.getLines());
  }

  @GetMapping("/{id}")
  public ResponseEntity<LineResponse> getLine(@PathVariable("id") Long id){
    return ResponseEntity.ok().body(lineService.getLine(id));
  }
}
