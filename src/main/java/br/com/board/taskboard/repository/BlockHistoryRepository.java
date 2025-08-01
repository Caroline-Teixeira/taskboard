package br.com.board.taskboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.board.taskboard.model.BlockHistory;
import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Card;

public interface BlockHistoryRepository extends JpaRepository<BlockHistory, Long>{

    List<BlockHistory> findByCard(Card card);
    Optional<BlockHistory> findByCardAndUnblockedDateIsNull(Card card);
    List<BlockHistory> findByCardTaskStatusBoard(Board board);
    void deleteByCardId(Long cardId);

}
