package br.com.board.taskboard.service;

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
import br.com.board.taskboard.util.DateUtil;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class CardService {
  

  private final CardRepository cardRepository;
  private final TaskStatusRepository taskStatusRepository;
  private final CardMovementRepository cardMovementRepository;
  private final BlockHistoryRepository blockHistoryRepository;
  private final BoardRepository boardRepository;

  

  @Autowired
  public CardService(
    CardRepository cardRepository,
    TaskStatusRepository taskStatusRepository,
    CardMovementRepository cardMovementRepository,
    BlockHistoryRepository blockHistoryRepository,
    BoardRepository boardRepository
  ) {
    this.cardRepository = cardRepository;
    this.taskStatusRepository = taskStatusRepository;
    this.cardMovementRepository = cardMovementRepository;
    this.blockHistoryRepository = blockHistoryRepository;
    this.boardRepository = boardRepository;
  }

  
  @Transactional
    public CardDTO findById(Long cardId) {
        Card card = cardRepository
                .findById(cardId)
                .orElseThrow(() ->
                        new TaskboardException("Cartão não encontrado com o ID: " + cardId)
                );

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


  @Transactional
  public CardDTO createCard(Long boardId, String title, String description) {
    if (title == null || title.isEmpty()) {
      throw new TaskboardException("Título do cartão não pode ser vazio.");
    }

    Board board = boardRepository
      .findById(boardId)
      .orElseThrow(() ->
        new TaskboardException("Quadro não encontrado com o ID: " + boardId)
      );
    TaskStatus initialStatus = taskStatusRepository
      .findByBoardAndStatus(board, Status.INICIAL)
      .orElseThrow(() ->
        new TaskboardException(
          "Coluna Inicial não encontrada para o quadro com ID: " + boardId
        )
      );

    
    Card card = new Card();
    card.setTitle(title);
    card.setDescription(description);
    card.setCreatedAt(DateUtil.now());
    card.setBlocked(false);
    card.setTaskStatus(initialStatus);
    card = cardRepository.save(card);

    
    CardMovement movement = new CardMovement();
    movement.setCard(card);
    movement.setTaskStatus(initialStatus);
    movement.setEntryDate(DateUtil.now());
    cardMovementRepository.save(movement);

    
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


  @Transactional
  public CardDTO moveCard(Long cardId, Long targetStatusId) {
    Card card = cardRepository
      .findById(cardId)
      .orElseThrow(() ->
        new TaskboardException("Cartão não encontrado com o ID: " + cardId)
      );

    
    if (card.isBlocked()) {
      throw new TaskboardException(
        "O cartão está bloqueado e não pode ser movido. ID do cartão: " + cardId
      );
    }

    TaskStatus currentStatus = card.getTaskStatus();
    TaskStatus targetStatus = taskStatusRepository
      .findById(targetStatusId)
      .orElseThrow(() ->
        new TaskboardException(
          "Coluna de destino não encontrada com ID: " + targetStatusId
        )
      );

    
    if (!currentStatus.getBoard().getId().equals(targetStatus.getBoard().getId())) {
            throw new TaskboardException(
                    "A coluna de destino não pertence ao mesmo quadro do cartão."
            );
        }


    if (
      targetStatus.getStatus() != Status.CANCELADA &&
      targetStatus.getPriority() != currentStatus.getPriority() + 1
    ) { // + 1 para mover para a próxima coluna
      throw new TaskboardException(
        "Só é possível mover para a próxima coluna ou para coluna de tasks CANCELADAS."
      );
    }

    CardMovement currentMovement = cardMovementRepository
      .findByCardAndExitDateIsNull(card)
      .orElseThrow(() ->
        new TaskboardException(
          "Nenhuma movimentação ativa encontrada para o cartão com ID: " +
          cardId
        )
      );
    currentMovement.setExitDate(DateUtil.now());
    cardMovementRepository.save(currentMovement);

  
    CardMovement newMovement = new CardMovement();
    newMovement.setCard(card);
    newMovement.setTaskStatus(targetStatus);
    newMovement.setEntryDate(DateUtil.now());
    cardMovementRepository.save(newMovement);

  
    card.setTaskStatus(targetStatus);
    card = cardRepository.save(card);

   
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

  
  @Transactional
  public BlockHistoryDTO blockCard(Long cardId, String blockReason) {
    if (blockReason == null || blockReason.trim().isEmpty()) {
      throw new TaskboardException("Motivo do bloqueio não pode ser vazio");
    }
    
    Card card = cardRepository
      .findById(cardId)
      .orElseThrow(() ->
        new TaskboardException("Cartão não encontrado com o ID: " + cardId)
      );

    if (card.isBlocked()) {
      throw new TaskboardException(
        "O cartão já está bloqueado. ID do cartão: " + cardId
      );
    }

    
    card.setBlocked(true);
    card.setBlockedReason(blockReason);
    card = cardRepository.save(card);

    
    BlockHistory blockHistory = new BlockHistory();
    blockHistory.setCard(card);
    blockHistory.setBlockedDate(DateUtil.now());
    blockHistory.setBlockedReason(blockReason);
    blockHistory = blockHistoryRepository.save(blockHistory);

    
    BlockHistoryDTO blockHistoryDTO = new BlockHistoryDTO();
    blockHistoryDTO.setId(blockHistory.getId());
    blockHistoryDTO.setCardId(blockHistory.getCard().getId());
    blockHistoryDTO.setBlockedDate(blockHistory.getBlockedDate());
    blockHistoryDTO.setBlockedReason(blockHistory.getBlockedReason());
    blockHistoryDTO.setUnblockedDate(blockHistory.getUnblockedDate());
    blockHistoryDTO.setUnblockedReason(blockHistory.getUnblockedReason());

    return blockHistoryDTO;
  }


  @Transactional
  public BlockHistoryDTO unblockCard(Long cardId, String unblockReason) {
    if (unblockReason == null || unblockReason.trim().isEmpty()) {
      throw new TaskboardException("Motivo do desbloqueio não pode ser vazio");
    }

    Card card = cardRepository
      .findById(cardId)
      .orElseThrow(() ->
        new TaskboardException("Cartão não encontrado com o ID: " + cardId)
      );

    if (!card.isBlocked()) {
      throw new TaskboardException(
        "O cartão não está bloqueado. ID do cartão: " + cardId
      );
    }

    card.setBlocked(false);
    card.setUnblockedReason(unblockReason);
    card = cardRepository.save(card);

    
    BlockHistory blockHistory = blockHistoryRepository
      .findByCardAndUnblockedDateIsNull(card)
      .orElseThrow(() ->
        new TaskboardException(
          "Nenhum histórico de bloqueio ativo encontrado para o cartão com ID: " +
          cardId
        )
      );
    blockHistory.setUnblockedDate(DateUtil.now());
    blockHistory.setUnblockedReason(unblockReason);
    blockHistoryRepository.save(blockHistory);

    
    BlockHistoryDTO blockHistoryDTO = new BlockHistoryDTO();
    blockHistoryDTO.setId(blockHistory.getId());
    blockHistoryDTO.setCardId(blockHistory.getCard().getId());
    blockHistoryDTO.setBlockedDate(blockHistory.getBlockedDate());
    blockHistoryDTO.setBlockedReason(blockHistory.getBlockedReason());
    blockHistoryDTO.setUnblockedDate(blockHistory.getUnblockedDate());
    blockHistoryDTO.setUnblockedReason(blockHistory.getUnblockedReason());

    return blockHistoryDTO;
  }

  @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardRepository
                .findById(cardId)
                .orElseThrow(() -> new TaskboardException("Cartão não encontrado com o ID: " + cardId));

        if (card.isBlocked()) {
            throw new TaskboardException("O cartão está bloqueado e não pode ser deletado. ID: " + cardId);
        }

        try {
            
            TaskStatus taskStatus = card.getTaskStatus();
            taskStatus.getCards().removeIf(cardItem -> cardItem.getId().equals(cardId));
            taskStatusRepository.saveAndFlush(taskStatus); // Forçar o commit da coleção
         
            cardMovementRepository.deleteByCardId(cardId);
            cardMovementRepository.flush();
            blockHistoryRepository.deleteByCardId(cardId);
            blockHistoryRepository.flush();
            
            cardRepository.deleteById(cardId);
            cardRepository.flush();
        } catch (Exception e) {
            throw new TaskboardException("Erro ao deletar cartão: " + e.getMessage());
        }
    }

}
