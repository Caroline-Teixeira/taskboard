package br.com.board.taskboard.service;


import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Card;
import br.com.board.taskboard.model.BlockHistory;
import br.com.board.taskboard.repository.BlockHistoryRepository;
import br.com.board.taskboard.repository.BoardRepository;
import br.com.board.taskboard.repository.CardRepository;
import br.com.board.taskboard.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BlockHistoryService {

    private final BlockHistoryRepository blockHistoryRepository;
    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;

    @Autowired
    public BlockHistoryService(BlockHistoryRepository blockHistoryRepository, BoardRepository boardRepository,
                              CardRepository cardRepository) {
        this.blockHistoryRepository = blockHistoryRepository;
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;
    }

    /**
     * Gera um relatório do histórico de bloqueios de cartões em um board ou de um cartão específico.
     * param boardId ID do board.
     * param cardId ID do cartão (opcional, pode ser nulo para todos os cartões).
     * return Lista de mapas com ID, cartão, datas, motivos e duração do bloqueio.
     */
    public List<Map<String, Object>> cardBlockHistory(Long boardId, Long cardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new TaskboardException("Quadro não encontrado com o ID: " + boardId));

        List<BlockHistory> blockHistories;
        if (cardId != null) {
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new TaskboardException("Cartão não encontrado com o ID: " + cardId));
            if (!card.getTaskStatus().getBoard().getId().equals(boardId)) {
                throw new TaskboardException("Cartão com ID " + cardId + " não pertence ao quadro com ID " + boardId);
            }
            blockHistories = blockHistoryRepository.findByCard(card);
        } else 
        {
            blockHistories = blockHistoryRepository.findByCardTaskStatusBoard(board);
        }

        return blockHistories.stream()
        .map(history -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", history.getId());
            result.put("cardId", history.getCard().getId());
            result.put("blockedDate", history.getBlockedDate());
            result.put("blockedReason", history.getBlockedReason());
            result.put("unblockedDate", history.getUnblockedDate());
            result.put("unblockedReason", history.getUnblockedReason());
            double blockDuration = DateUtil.calculateHours(history.getBlockedDate(), history.getUnblockedDate());
            result.put("blockedDuration", blockDuration);
            return result;
        }).collect(Collectors.toList());
    }

    /**
     * Lista os bloqueios ativos (sem data de desbloqueio) de cartões em um board.
     * param boardId ID do board.
     * return Lista de mapas com ID, cartão, datas, motivos e duração do bloqueio ativo.
     */
    public List<Map<String, Object>> activeBlocksByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new TaskboardException("Quadro não encontrado com o ID: " + boardId));

        List<BlockHistory> blockHistories = blockHistoryRepository.findByCardTaskStatusBoard(board);
        return blockHistories.stream()
                .filter(history -> history.getUnblockedDate() == null)
                .map(history -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", history.getId());
                    result.put("cardId", history.getCard().getId());
                    result.put("blockedDate", history.getBlockedDate());
                    result.put("blockedReason", history.getBlockedReason());
                    result.put("unblockedDate", history.getUnblockedDate());
                    result.put("unblockedReason", history.getUnblockedReason());
                    double blockDuration = DateUtil.calculateHours(history.getBlockedDate(), null);
                    result.put("blockedDuration", blockDuration);
                    return result;
                }).collect(Collectors.toList());
    }
}