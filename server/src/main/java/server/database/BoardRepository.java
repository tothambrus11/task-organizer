package server.database;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import commons.models.Board;

public interface BoardRepository extends JpaRepository<Board, UUID> {

    Optional<Board> findByKey(String key);

}
