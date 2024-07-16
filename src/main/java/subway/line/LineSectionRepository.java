package subway.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineSectionRepository extends JpaRepository<LineSection, Long> {

  @Query("SELECT ls FROM LineSection ls JOIN ls.line ON ls.line.id = :lineId")
  List<LineSection> findAllByLineId(Long lineId);
}
