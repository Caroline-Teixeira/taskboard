package br.com.board.taskboard.service;

import br.com.board.taskboard.dto.CardMovementDTO;
import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Card;
import br.com.board.taskboard.model.CardMovement;
import br.com.board.taskboard.repository.BoardRepository;
import br.com.board.taskboard.repository.CardRepository;
import br.com.board.taskboard.repository.CardMovementRepository;
import br.com.board.taskboard.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardMovementService {

    private final CardMovementRepository cardMovementRepository;
    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CardMovementService(CardMovementRepository cardMovementRepository, BoardRepository boardRepository,
                              CardRepository cardRepository) {
        this.cardMovementRepository = cardMovementRepository;
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;
    }

    /**
     * Gera um relatório do tempo que cada cartão passou em cada coluna de um board.
     * param boardId ID do board.
     * return Lista de mapas com ID da movimentação, cartão, coluna, datas e tempo em horas.
     */
    public List<Map<String, Object>> cardTimeInColumn(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new TaskboardException("Quadro não encontrado com o ID: " + boardId));

        List<CardMovement> movements = cardMovementRepository.findByCardTaskStatusBoard(board);
        return movements.stream()
        .map(movement -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", movement.getId());
            result.put("cardId", movement.getCard().getId());
            result.put("taskStatusId", movement.getTaskStatus().getId());
            result.put("entryDate", movement.getEntryDate());
            result.put("exitDate", movement.getExitDate());
            double timeInHours = DateUtil.calculateHours(movement.getEntryDate(), movement.getExitDate());
            result.put("timeInHours", timeInHours);
            return result;
        }).collect(Collectors.toList());
    }

    /**
     * Lista todas as movimentações de um cartão.
     * param cardId ID do cartão.
     * return Lista de movimentações do cartão.
     */
    public List<CardMovementDTO> listMovements(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new TaskboardException("Cartão não encontrado com o ID: " + cardId));

        List<CardMovement> movements = cardMovementRepository.findByCard(card);
        return movements.stream().map(movement -> {
            CardMovementDTO dto = new CardMovementDTO();
            dto.setId(movement.getId());
            dto.setCardId(movement.getCard().getId());
            dto.setTaskStatusId(movement.getTaskStatus().getId());
            dto.setEntryDate(movement.getEntryDate());
            dto.setExitDate(movement.getExitDate());
            return dto;
        }).collect(Collectors.toList());
    }
}