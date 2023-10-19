package server.database;

import commons.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findByTaskListId(UUID taskListId);

}


