package server.database;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import commons.models.SubTask;

public interface SubTaskRepository extends JpaRepository<SubTask, String> {

    List<SubTask> findByTaskId(UUID taskId);

}
