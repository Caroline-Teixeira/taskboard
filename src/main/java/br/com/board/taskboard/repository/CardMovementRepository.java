package br.com.board.taskboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Card;
import br.com.board.taskboard.model.CardMovement;

public interface CardMovementRepository extends JpaRepository<CardMovement, Long> {

    List<CardMovement> findByCard(Card card);
    Optional<CardMovement> findByCardAndExitDateIsNull(Card card);
    List<CardMovement> findByCardTaskStatusBoard(Board board);

}
