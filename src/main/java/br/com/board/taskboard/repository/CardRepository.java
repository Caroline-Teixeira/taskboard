package br.com.board.taskboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import br.com.board.taskboard.model.Card;
import br.com.board.taskboard.model.TaskStatus;

public interface CardRepository extends JpaRepository<Card, Long>{

    List<Card> findByTaskStatus(TaskStatus taskStatus);

}
