package server.database;

import commons.models.TaskHighlight;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskHighlightRepository extends JpaRepository<TaskHighlight, String> {

    List<TaskHighlight> findByBoardId(UUID boardId);

}
