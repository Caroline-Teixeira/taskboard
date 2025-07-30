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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @Mock
    private CardMovementRepository cardMovementRepository;

    @Mock
    private BlockHistoryRepository blockHistoryRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCard_ValidInput_ReturnsCardDTO() {
        Long boardId = 1L;
        String title = "Tarefa Teste";
        String description = "Descrição Teste";
        Board board = new Board();
        board.setId(boardId);
        TaskStatus initialStatus = new TaskStatus();
        initialStatus.setId(2L);
        initialStatus.setStatus(Status.INICIAL);
        Card card = new Card();
        card.setId(3L);
        card.setTitle(title);
        card.setDescription(description);
        LocalDateTime now = LocalDateTime.of(2025, 7, 28, 18, 0);
        card.setCreatedAt(now);
        card.setBlocked(false);
        card.setTaskStatus(initialStatus);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(taskStatusRepository.findByBoardAndStatus(board, Status.INICIAL)).thenReturn(Optional.of(initialStatus));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMovementRepository.save(any(CardMovement.class))).thenReturn(new CardMovement());
        try (MockedStatic<DateUtil> dateUtil = Mockito.mockStatic(DateUtil.class)) {
            dateUtil.when(() -> DateUtil.now()).thenReturn(now);

            CardDTO result = cardService.createCard(boardId, title, description);

            assertEquals(3L, result.getId());
            assertEquals(title, result.getTitle());
            assertEquals(description, result.getDescription());
            assertEquals(now, result.getCreatedAt());
            assertFalse(result.isBlocked());
            assertEquals(2L, result.getTaskStatusId());
            verify(boardRepository).findById(boardId);
            verify(taskStatusRepository).findByBoardAndStatus(board, Status.INICIAL);
            verify(cardRepository).save(any(Card.class));
            verify(cardMovementRepository).save(any(CardMovement.class));
        }
    }

    @Test
    void createCard_EmptyTitle_ThrowsTaskboardException() {
        Long boardId = 1L;
        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardService.createCard(boardId, "", "Descrição"));
        assertEquals("Título do cartão não pode ser vazio.", exception.getMessage());
        verifyNoInteractions(boardRepository, taskStatusRepository, cardRepository, cardMovementRepository);
    }

    @Test
    void createCard_BoardNotFound_ThrowsTaskboardException() {
        Long boardId = 1L;
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardService.createCard(boardId, "Tarefa", "Descrição"));
        assertEquals("Quadro não encontrado com o ID: " + boardId, exception.getMessage());
        verify(boardRepository).findById(boardId);
        verifyNoInteractions(taskStatusRepository, cardRepository, cardMovementRepository);
    }

    @Test
    void findById_ValidCardId_ReturnsCardDTO() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        card.setTitle("Tarefa Teste");
        card.setDescription("Descrição Teste");
        LocalDateTime now = LocalDateTime.of(2025, 7, 28, 18, 0);
        card.setCreatedAt(now);
        card.setBlocked(false);
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(2L);
        card.setTaskStatus(taskStatus);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        CardDTO result = cardService.findById(cardId);

        assertEquals(cardId, result.getId());
        assertEquals("Tarefa Teste", result.getTitle());
        assertEquals("Descrição Teste", result.getDescription());
        assertEquals(now, result.getCreatedAt());
        assertFalse(result.isBlocked());
        assertEquals(2L, result.getTaskStatusId());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void findById_CardNotFound_ThrowsTaskboardException() {
        Long cardId = 1L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardService.findById(cardId));
        assertEquals("Cartão não encontrado com o ID: " + cardId, exception.getMessage());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void moveCard_ValidInput_ReturnsCardDTO() {
        Long cardId = 1L;
        Long targetStatusId = 2L;
        Card card = new Card();
        card.setId(cardId);
        card.setBlocked(false);
        TaskStatus currentStatus = new TaskStatus();
        currentStatus.setId(3L);
        currentStatus.setPriority(1);
        Board board = new Board();
        board.setId(4L);
        currentStatus.setBoard(board);
        card.setTaskStatus(currentStatus);
        TaskStatus targetStatus = new TaskStatus();
        targetStatus.setId(targetStatusId);
        targetStatus.setPriority(2);
        targetStatus.setBoard(board);
        CardMovement currentMovement = new CardMovement();
        currentMovement.setCard(card);
        currentMovement.setTaskStatus(currentStatus);
        LocalDateTime now = LocalDateTime.of(2025, 7, 28, 18, 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(taskStatusRepository.findById(targetStatusId)).thenReturn(Optional.of(targetStatus));
        when(cardMovementRepository.findByCardAndExitDateIsNull(card)).thenReturn(Optional.of(currentMovement));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMovementRepository.save(any(CardMovement.class))).thenReturn(new CardMovement());
        try (MockedStatic<DateUtil> dateUtil = Mockito.mockStatic(DateUtil.class)) {
            dateUtil.when(() -> DateUtil.now()).thenReturn(now);

            CardDTO result = cardService.moveCard(cardId, targetStatusId);

            assertEquals(cardId, result.getId());
            assertEquals(targetStatusId, result.getTaskStatusId());
            verify(cardRepository).findById(cardId);
            verify(taskStatusRepository).findById(targetStatusId);
            verify(cardMovementRepository).findByCardAndExitDateIsNull(card);
            verify(cardMovementRepository, times(2)).save(any(CardMovement.class));
            verify(cardRepository).save(any(Card.class));
        }
    }

    @Test
    void moveCard_CardBlocked_ThrowsTaskboardException() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        card.setBlocked(true);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardService.moveCard(cardId, 2L));
        assertEquals("O cartão está bloqueado e não pode ser movido. ID do cartão: " + cardId, exception.getMessage());
        verify(cardRepository).findById(cardId);
        verifyNoInteractions(taskStatusRepository, cardMovementRepository);
    }

    @Test
    void moveCard_DifferentBoard_ThrowsTaskboardException() {
        Long cardId = 1L;
        Long targetStatusId = 2L;
        Card card = new Card();
        card.setId(cardId);
        card.setBlocked(false);
        TaskStatus currentStatus = new TaskStatus();
        currentStatus.setId(3L);
        Board board1 = new Board();
        board1.setId(4L);
        currentStatus.setBoard(board1);
        card.setTaskStatus(currentStatus);
        TaskStatus targetStatus = new TaskStatus();
        targetStatus.setId(targetStatusId);
        Board board2 = new Board();
        board2.setId(5L);
        targetStatus.setBoard(board2);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(taskStatusRepository.findById(targetStatusId)).thenReturn(Optional.of(targetStatus));

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardService.moveCard(cardId, targetStatusId));
        assertEquals("A coluna de destino não pertence ao mesmo quadro do cartão.", exception.getMessage());
        verify(cardRepository).findById(cardId);
        verify(taskStatusRepository).findById(targetStatusId);
        verifyNoInteractions(cardMovementRepository);
    }

    @Test
    void blockCard_ValidInput_ReturnsBlockHistoryDTO() {
        Long cardId = 1L;
        String blockReason = "Motivo X";
        Card card = new Card();
        card.setId(cardId);
        card.setBlocked(false);
        BlockHistory blockHistory = new BlockHistory();
        blockHistory.setId(2L);
        blockHistory.setCard(card);
        LocalDateTime now = LocalDateTime.of(2025, 7, 28, 18, 0);
        blockHistory.setBlockedDate(now);
        blockHistory.setBlockedReason(blockReason);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(blockHistoryRepository.save(any(BlockHistory.class))).thenReturn(blockHistory);
        try (MockedStatic<DateUtil> dateUtil = Mockito.mockStatic(DateUtil.class)) {
            dateUtil.when(() -> DateUtil.now()).thenReturn(now);

            BlockHistoryDTO result = cardService.blockCard(cardId, blockReason);

            assertEquals(2L, result.getId());
            assertEquals(cardId, result.getCardId());
            assertEquals(now, result.getBlockedDate());
            assertEquals(blockReason, result.getBlockedReason());
            assertNull(result.getUnblockedDate());
            assertNull(result.getUnblockedReason());
            verify(cardRepository).findById(cardId);
            verify(cardRepository).save(any(Card.class));
            verify(blockHistoryRepository).save(any(BlockHistory.class));
        }
    }

    @Test
    void blockCard_EmptyReason_ThrowsTaskboardException() {
        Long cardId = 1L;
        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardService.blockCard(cardId, ""));
        assertEquals("Motivo do bloqueio não pode ser vazio", exception.getMessage());
        verifyNoInteractions(cardRepository, blockHistoryRepository);
    }

    @Test
    void blockCard_CardAlreadyBlocked_ThrowsTaskboardException() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        card.setBlocked(true);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardService.blockCard(cardId, "Motivo X"));
        assertEquals("O cartão já está bloqueado. ID do cartão: " + cardId, exception.getMessage());
        verify(cardRepository).findById(cardId);
        verifyNoInteractions(blockHistoryRepository);
    }

    @Test
    void unblockCard_ValidInput_ReturnsBlockHistoryDTO() {
        Long cardId = 1L;
        String unblockReason = "Motivo Y";
        Card card = new Card();
        card.setId(cardId);
        card.setBlocked(true);
        BlockHistory blockHistory = new BlockHistory();
        blockHistory.setId(2L);
        blockHistory.setCard(card);
        LocalDateTime now = LocalDateTime.of(2025, 7, 28, 18, 0);
        blockHistory.setBlockedDate(now);
        blockHistory.setBlockedReason("Motivo X");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(blockHistoryRepository.findByCardAndUnblockedDateIsNull(card)).thenReturn(Optional.of(blockHistory));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(blockHistoryRepository.save(any(BlockHistory.class))).thenReturn(blockHistory);
        try (MockedStatic<DateUtil> dateUtil = Mockito.mockStatic(DateUtil.class)) {
            dateUtil.when(() -> DateUtil.now()).thenReturn(now);

            BlockHistoryDTO result = cardService.unblockCard(cardId, unblockReason);

            assertEquals(2L, result.getId());
            assertEquals(cardId, result.getCardId());
            assertEquals(now, result.getBlockedDate());
            assertEquals("Motivo X", result.getBlockedReason());
            assertEquals(now, result.getUnblockedDate());
            assertEquals(unblockReason, result.getUnblockedReason());
            verify(cardRepository).findById(cardId);
            verify(blockHistoryRepository).findByCardAndUnblockedDateIsNull(card);
            verify(cardRepository).save(any(Card.class));
            verify(blockHistoryRepository).save(any(BlockHistory.class));
        }
    }

    @Test
    void unblockCard_EmptyReason_ThrowsTaskboardException() {
        Long cardId = 1L;
        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardService.unblockCard(cardId, ""));
        assertEquals("Motivo do desbloqueio não pode ser vazio", exception.getMessage());
        verifyNoInteractions(cardRepository, blockHistoryRepository);
    }

    @Test
    void unblockCard_CardNotBlocked_ThrowsTaskboardException() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        card.setBlocked(false);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardService.unblockCard(cardId, "Motivo Y"));
        assertEquals("O cartão não está bloqueado. ID do cartão: " + cardId, exception.getMessage());
        verify(cardRepository).findById(cardId);
        verifyNoInteractions(blockHistoryRepository);
    }
}