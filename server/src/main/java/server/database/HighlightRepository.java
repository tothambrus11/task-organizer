package server.database;

import commons.models.TaskHighlight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HighlightRepository extends JpaRepository<TaskHighlight, UUID> {
}
