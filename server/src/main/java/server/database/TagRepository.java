package server.database;

import commons.models.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, String> {

    List<Tag> findByBoardId(UUID boardId);

    @Query(value = "SELECT * FROM TAG INNER JOIN TASK_TAG ON TAG.ID = TASK_TAG.TAG_ID INNER JOIN TASK ON TASK_TAG.TASK_ID = TASK.ID WHERE TASK.ID = :taskId", nativeQuery = true)
    List<Tag> findByTaskId(@Param("taskId") UUID id);
}
