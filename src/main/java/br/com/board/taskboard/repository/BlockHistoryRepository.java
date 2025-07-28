package br.com.board.taskboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.board.taskboard.model.BlockHistory;
import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Card;

public interface BlockHistoryRepository extends JpaRepository<BlockHistory, Long>{

    List<BlockHistory> findByCard(Card card);
    Optional<BlockHistory> findByCardAndUnblockDateIsNull(Card card);
    List<BlockHistory> findByBoard (Board board);

}
