package br.com.board.taskboard.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.board.taskboard.dto.BlockHistoryDTO;
import br.com.board.taskboard.dto.CardDTO;
import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.model.BlockHistory;
import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Card;
import br.com.board.taskboard.model.CardMovement;
import br.com.board.taskboard.model.Status;
import br.com.board.taskboard.model.TaskStatus;
import br.com.board.taskboard.repository.BlockHistoryRepository;
import br.com.board.taskboard.repository.BoardRepository;
import br.com.board.taskboard.repository.CardMovementRepository;
import br.com.board.taskboard.repository.CardRepository;
import br.com.board.taskboard.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;

public class CardService {

    private final CardRepository cardRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final CardMovementRepository cardMovementRepository;
    private final BlockHistoryRepository blockHistoryRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public CardService(CardRepository cardRepository,
                        TaskStatusRepository taskStatusRepository,
                        CardMovementRepository cardMovementRepository,
                        BlockHistoryRepository blockHistoryRepository,
                        BoardRepository boardRepository) {
        this.cardRepository = cardRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.cardMovementRepository = cardMovementRepository;
        this.blockHistoryRepository = blockHistoryRepository;
        this.boardRepository = boardRepository;
    }

    // criar cartão
    @Transactional
    public CardDTO createCard(Long boardId, String title, String description) {
        if (title == null || title.isEmpty()) {
            throw new TaskboardException("Título do cartão não pode ser vazio.");
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new TaskboardException("Quadro não encontrado com o ID: " + boardId));
        TaskStatus initialStatus = taskStatusRepository.findByBoardAndStatus(board, Status.INICIAL)
                .orElseThrow(() -> new TaskboardException("Coluna Inicial não encontrada para o quadro com ID: " + boardId));

        // Cria o cartão para persistência
        Card card = new Card();
        card.setTitle(title);
        card.setDescription(description);
        card.setCreatedAt(LocalDateTime.now());
        card.setBlocked(false);
        card.setTaskStatus(initialStatus);
        card = cardRepository.save(card);

        // movimenta o cartão
        CardMovement movement = new CardMovement();
        movement.setCard(card);
        movement.setTaskStatus(initialStatus);
        movement.setEntryDate(LocalDateTime.now());
        cardMovementRepository.save(movement);


        // Cria o DTO para retorno no menu
        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(card.getId());
        cardDTO.setTitle(card.getTitle());
        cardDTO.setDescription(card.getDescription());
        cardDTO.setCreatedAt(card.getCreatedAt());
        cardDTO.setBlocked(card.isBlocked());
        cardDTO.setBlockedReason(card.getBlockedReason());
        cardDTO.setUnblockedReason(card.getUnblockedReason());
        cardDTO.setTaskStatusId(card.getTaskStatus().getId());

        return cardDTO;
    }

    // mover cartão
    @Transactional
    public CardDTO moveCard(Long cardId, Long targetStatusId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new TaskboardException("Cartão não encontrado com o ID: " + cardId));
        

        // Verifica se o cartão está bloqueado
        if (card.isBlocked()) {
            throw new TaskboardException("O cartão está bloqueado e não pode ser movido. ID do cartão: " + cardId);
        }

        TaskStatus currentStatus = card.getTaskStatus();
        TaskStatus targetStatus = taskStatusRepository.findById(targetStatusId)
                .orElseThrow(() -> new TaskboardException("Coluna de destino não encontrada com ID: " + targetStatusId));

        // Verifica se o status de destino é válido
        if (targetStatus.getStatus() != Status.CANCELADA &&
                targetStatus.getPriority() != currentStatus.getPriority() + 1) { // + 1 para mover para a próxima coluna
            throw new TaskboardException("Só é possível mover para a próxima coluna ou para coluna de tasks CANCELADAS." );
        }

        // Atualiza a movimentação atual do cartão
        CardMovement currentMovement = cardMovementRepository.findByCardAndExitDateIsNull(card)
                .orElseThrow(() -> new TaskboardException("Nenhuma movimentação ativa encontrada para o cartão com ID: " + cardId));
        currentMovement.setExitDate(LocalDateTime.now());
        cardMovementRepository.save(currentMovement);

        // Cria uma nova movimentação para o cartão
        CardMovement newMovement = new CardMovement();
        newMovement.setCard(card);
        newMovement.setTaskStatus(targetStatus);
        newMovement.setEntryDate(LocalDateTime.now());
        cardMovementRepository.save(newMovement);

        // Atualiza o cartão com o novo status
        card.setTaskStatus(targetStatus);
        card = cardRepository.save(card);

        // Cria o DTO para retorno no menu
        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(card.getId());
        cardDTO.setTitle(card.getTitle());
        cardDTO.setDescription(card.getDescription());
        cardDTO.setCreatedAt(card.getCreatedAt());
        cardDTO.setBlocked(card.isBlocked());
        cardDTO.setBlockedReason(card.getBlockedReason());
        cardDTO.setUnblockedReason(card.getUnblockedReason());
        cardDTO.setTaskStatusId(card.getTaskStatus().getId());

        return cardDTO;
    }

    // cartão bloqueado
    @Transactional
    public BlockHistoryDTO blockCard(Long cardId, String blockReason) {
        if (blockReason == null || blockReason.trim().isEmpty()) {
            throw new TaskboardException("Motivo do bloqueio não pode ser vazio");
        
        }
        // Verifica se o cartão existe
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new TaskboardException("Cartão não encontrado com o ID: " + cardId));

        if (card.isBlocked()) {
            throw new TaskboardException("O cartão já está bloqueado. ID do cartão: " + cardId);
        }

        // Bloqueia o cartão
        card.setBlocked(true);
        card.setBlockedReason(blockReason);
        card = cardRepository.save(card);

        // Cria o histórico de bloqueio - persistência
        BlockHistory blockHistory = new BlockHistory();
        blockHistory.setCard(card);
        blockHistory.setBlockedDate(LocalDateTime.now());
        blockHistory.setBlockedReason(blockReason);
        blockHistory = blockHistoryRepository.save(blockHistory);

        // Cria o DTO para retorno no menu
        BlockHistoryDTO blockHistoryDTO = new BlockHistoryDTO();
        blockHistoryDTO.setId(blockHistory.getId());
        blockHistoryDTO.setCardId(blockHistory.getCard().getId());
        blockHistoryDTO.setBlockDate(blockHistory.getBlockedDate());
        blockHistoryDTO.setBlockReason(blockHistory.getBlockedReason());
        blockHistoryDTO.setUnblockDate(blockHistory.getUnblockedDate());
        blockHistoryDTO.setUnblockReason(blockHistory.getUnblockedReason());


        return blockHistoryDTO;
    }

    // desbloquear cartão
    @Transactional
    public BlockHistoryDTO unblockCard(Long cardId, String unblockReason) {
        if (unblockReason == null || unblockReason.trim().isEmpty()) {
            throw new TaskboardException("Motivo do desbloqueio não pode ser vazio");
        }

        // Verifica se o cartão existe
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new TaskboardException("Cartão não encontrado com o ID: " + cardId));

        if (!card.isBlocked()) {
            throw new TaskboardException("O cartão não está bloqueado. ID do cartão: " + cardId);
        }

        // Desbloqueia o cartão
        card.setBlocked(false);
        card.setUnblockedReason(unblockReason);
        card = cardRepository.save(card);

        // Atualiza o histórico de bloqueio
       BlockHistory blockHistory = blockHistoryRepository.findByCardAndUnblockDateIsNull(card)
                .orElseThrow(() -> new TaskboardException("Nenhum histórico de bloqueio ativo encontrado para o cartão com ID: " + cardId));
        blockHistory.setUnblockedDate(LocalDateTime.now());
        blockHistory.setUnblockedReason(unblockReason);
        blockHistoryRepository.save(blockHistory);

        // Cria o DTO para retorno no menu
        BlockHistoryDTO blockHistoryDTO = new BlockHistoryDTO();
        blockHistoryDTO.setId(blockHistory.getId());
        blockHistoryDTO.setCardId(blockHistory.getCard().getId());
        blockHistoryDTO.setBlockDate(blockHistory.getBlockedDate());
        blockHistoryDTO.setBlockReason(blockHistory.getBlockedReason());
        blockHistoryDTO.setUnblockDate(blockHistory.getUnblockedDate());
        blockHistoryDTO.setUnblockReason(blockHistory.getUnblockedReason());

        return blockHistoryDTO;
    }
}
