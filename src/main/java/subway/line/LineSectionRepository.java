package subway.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineSectionRepository extends JpaRepository<LineSection, Long> {

  List<LineSection> findAllByLineId(Long lineId);
}
