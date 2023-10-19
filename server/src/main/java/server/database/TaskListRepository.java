package server.database;

import commons.models.TaskList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskListRepository extends JpaRepository<TaskList, String> {

    List<TaskList> findByBoardId(UUID boardId);

}
