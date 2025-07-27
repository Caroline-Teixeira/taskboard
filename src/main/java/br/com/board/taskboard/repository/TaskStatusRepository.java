package br.com.board.taskboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Status;
import br.com.board.taskboard.model.TaskStatus;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    List<TaskStatus> findByBoardOrderByPriority(Board board);
    Optional<TaskStatus> findByBoardAndStatus(Board board, Status status);

}
