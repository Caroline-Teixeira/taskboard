package br.com.board.taskboard.service;

import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Card;
import br.com.board.taskboard.model.BlockHistory;
import br.com.board.taskboard.model.TaskStatus;
import br.com.board.taskboard.repository.BlockHistoryRepository;
import br.com.board.taskboard.repository.BoardRepository;
import br.com.board.taskboard.repository.CardRepository;
import br.com.board.taskboard.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockHistoryServiceTest {

    @Mock
    private BlockHistoryRepository blockHistoryRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private BlockHistoryService blockHistoryService;

    @Test
    void cardBlockHistory_ValidBoardIdNoCardId_ReturnsBlockHistoryList() {
        Long boardId = 1L;
        Board board = new Board();
        board.setId(boardId);

        Card card = new Card();
        card.setId(2L);
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(3L);
        taskStatus.setBoard(board);
        card.setTaskStatus(taskStatus);

        BlockHistory history1 = new BlockHistory();
        history1.setId(4L);
        history1.setCard(card);
        LocalDateTime blockedDate1 = LocalDateTime.of(2025, 7, 28, 10, 0);
        LocalDateTime unblockedDate1 = LocalDateTime.of(2025, 7, 28, 12, 0);
        history1.setBlockedDate(blockedDate1);
        history1.setBlockedReason("Motivo 1");
        history1.setUnblockedDate(unblockedDate1);
        history1.setUnblockedReason("Desbloqueado 1");

        BlockHistory history2 = new BlockHistory();
        history2.setId(5L);
        history2.setCard(card);
        LocalDateTime blockedDate2 = LocalDateTime.of(2025, 7, 28, 13, 0);
        history2.setBlockedDate(blockedDate2);
        history2.setBlockedReason("Motivo 2");
        history2.setUnblockedDate(null);
        history2.setUnblockedReason(null);

        List<BlockHistory> histories = Arrays.asList(history1, history2);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(blockHistoryRepository.findByBoard(board)).thenReturn(histories);
        try (MockedStatic<DateUtil> dateUtil = mockStatic(DateUtil.class)) {
            dateUtil.when(() -> DateUtil.calculateHours(blockedDate1, unblockedDate1)).thenReturn(2.0);
            dateUtil.when(() -> DateUtil.calculateHours(blockedDate2, null)).thenReturn(0.0);

            List<Map<String, Object>> result = blockHistoryService.cardBlockHistory(boardId, null);

            assertEquals(2, result.size());

            Map<String, Object> result1 = result.get(0);
            assertEquals(4L, result1.get("id"));
            assertEquals(2L, result1.get("cardId"));
            assertEquals(blockedDate1, result1.get("blockedDate"));
            assertEquals("Motivo 1", result1.get("blockedReason"));
            assertEquals(unblockedDate1, result1.get("unblockedDate"));
            assertEquals("Desbloqueado 1", result1.get("unblockedReason"));
            assertEquals(2.0, result1.get("blockedDuration"));

            Map<String, Object> result2 = result.get(1);
            assertEquals(5L, result2.get("id"));
            assertEquals(2L, result2.get("cardId"));
            assertEquals(blockedDate2, result2.get("blockedDate"));
            assertEquals("Motivo 2", result2.get("blockedReason"));
            assertNull(result2.get("unblockedDate"));
            assertNull(result2.get("unblockedReason"));
            assertEquals(0.0, result2.get("blockedDuration"));

            verify(boardRepository).findById(boardId);
            verify(blockHistoryRepository).findByBoard(board);
            verifyNoInteractions(cardRepository);
        }
    }

    @Test
    void cardBlockHistory_ValidBoardIdAndCardId_ReturnsBlockHistoryList() {
        Long boardId = 1L;
        Long cardId = 2L;
        Board board = new Board();
        board.setId(boardId);
        Card card = new Card();
        card.setId(cardId);
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(3L);
        taskStatus.setBoard(board);
        card.setTaskStatus(taskStatus);

        BlockHistory history = new BlockHistory();
        history.setId(4L);
        history.setCard(card);
        LocalDateTime blockedDate = LocalDateTime.of(2025, 7, 28, 10, 0);
        LocalDateTime unblockedDate = LocalDateTime.of(2025, 7, 28, 12, 0);
        history.setBlockedDate(blockedDate);
        history.setBlockedReason("Motivo 1");
        history.setUnblockedDate(unblockedDate);
        history.setUnblockedReason("Desbloqueado 1");

        List<BlockHistory> histories = Arrays.asList(history);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(blockHistoryRepository.findByCard(card)).thenReturn(histories);
        try (MockedStatic<DateUtil> dateUtil = mockStatic(DateUtil.class)) {
            dateUtil.when(() -> DateUtil.calculateHours(blockedDate, unblockedDate)).thenReturn(2.0);

            List<Map<String, Object>> result = blockHistoryService.cardBlockHistory(boardId, cardId);

            assertEquals(1, result.size());
            Map<String, Object> result1 = result.get(0);
            assertEquals(4L, result1.get("id"));
            assertEquals(cardId, result1.get("cardId"));
            assertEquals(blockedDate, result1.get("blockedDate"));
            assertEquals("Motivo 1", result1.get("blockedReason"));
            assertEquals(unblockedDate, result1.get("unblockedDate"));
            assertEquals("Desbloqueado 1", result1.get("unblockedReason"));
            assertEquals(2.0, result1.get("blockedDuration"));

            verify(boardRepository).findById(boardId);
            verify(cardRepository).findById(cardId);
            verify(blockHistoryRepository).findByCard(card);
        }
    }

    @Test
    void cardBlockHistory_BoardNotFound_ThrowsTaskboardException() {
        Long boardId = 1L;
        Long cardId = 2L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> blockHistoryService.cardBlockHistory(boardId, cardId));
        assertEquals("Quadro não encontrado com o ID: " + boardId, exception.getMessage());
        verify(boardRepository).findById(boardId);
        verifyNoInteractions(cardRepository, blockHistoryRepository);
    }

    @Test
    void cardBlockHistory_CardNotFound_ThrowsTaskboardException() {
        Long boardId = 1L;
        Long cardId = 2L;
        Board board = new Board();
        board.setId(boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> blockHistoryService.cardBlockHistory(boardId, cardId));
        assertEquals("Cartão não encontrado com o ID: " + cardId, exception.getMessage());
        verify(boardRepository).findById(boardId);
        verify(cardRepository).findById(cardId);
        verifyNoInteractions(blockHistoryRepository);
    }

    @Test
    void cardBlockHistory_CardNotInBoard_ThrowsTaskboardException() {
        Long boardId = 1L;
        Long cardId = 2L;
        Board board = new Board();
        board.setId(boardId);
        Card card = new Card();
        card.setId(cardId);
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(3L);
        Board otherBoard = new Board();
        otherBoard.setId(4L);
        taskStatus.setBoard(otherBoard);
        card.setTaskStatus(taskStatus);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> blockHistoryService.cardBlockHistory(boardId, cardId));
        assertEquals("Cartão com ID " + cardId + " não pertence ao quadro com ID " + boardId, exception.getMessage());
        verify(boardRepository).findById(boardId);
        verify(cardRepository).findById(cardId);
        verifyNoInteractions(blockHistoryRepository);
    }

    @Test
    void cardBlockHistory_NoHistories_ReturnsEmptyList() {
        Long boardId = 1L;
        Board board = new Board();
        board.setId(boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(blockHistoryRepository.findByBoard(board)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = blockHistoryService.cardBlockHistory(boardId, null);

        assertTrue(result.isEmpty());
        verify(boardRepository).findById(boardId);
        verify(blockHistoryRepository).findByBoard(board);
        verifyNoInteractions(cardRepository);
    }

    @Test
    void cardBlockHistory_NoHistoriesForCard_ReturnsEmptyList() {
        Long boardId = 1L;
        Long cardId = 2L;
        Board board = new Board();
        board.setId(boardId);
        Card card = new Card();
        card.setId(cardId);
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(3L);
        taskStatus.setBoard(board);
        card.setTaskStatus(taskStatus);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(blockHistoryRepository.findByCard(card)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = blockHistoryService.cardBlockHistory(boardId, cardId);

        assertTrue(result.isEmpty());
        verify(boardRepository).findById(boardId);
        verify(cardRepository).findById(cardId);
        verify(blockHistoryRepository).findByCard(card);
    }

    @Test
    void activeBlocksByBoard_ValidBoardId_ReturnsActiveBlockList() {
        Long boardId = 1L;
        Board board = new Board();
        board.setId(boardId);

        Card card = new Card();
        card.setId(2L);
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(3L);
        taskStatus.setBoard(board);
        card.setTaskStatus(taskStatus);

        BlockHistory history1 = new BlockHistory();
        history1.setId(4L);
        history1.setCard(card);
        LocalDateTime blockedDate1 = LocalDateTime.of(2025, 7, 28, 10, 0);
        history1.setBlockedDate(blockedDate1);
        history1.setBlockedReason("Motivo 1");
        history1.setUnblockedDate(null);
        history1.setUnblockedReason(null);

        BlockHistory history2 = new BlockHistory();
        history2.setId(5L);
        history2.setCard(card);
        LocalDateTime blockedDate2 = LocalDateTime.of(2025, 7, 28, 12, 0);
        history2.setBlockedDate(blockedDate2);
        history2.setBlockedReason("Motivo 2");
        history2.setUnblockedDate(LocalDateTime.of(2025, 7, 28, 14, 0));
        history2.setUnblockedReason("Desbloqueado 2");

        List<BlockHistory> histories = Arrays.asList(history1, history2);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(blockHistoryRepository.findByBoard(board)).thenReturn(histories);
        try (MockedStatic<DateUtil> dateUtil = mockStatic(DateUtil.class)) {
            dateUtil.when(() -> DateUtil.calculateHours(blockedDate1, null)).thenReturn(0.0);

            List<Map<String, Object>> result = blockHistoryService.activeBlocksByBoard(boardId);

            assertEquals(1, result.size());
            Map<String, Object> result1 = result.get(0);
            assertEquals(4L, result1.get("id"));
            assertEquals(2L, result1.get("cardId"));
            assertEquals(blockedDate1, result1.get("blockedDate"));
            assertEquals("Motivo 1", result1.get("blockedReason"));
            assertNull(result1.get("unblockedDate"));
            assertNull(result1.get("unblockedReason"));
            assertEquals(0.0, result1.get("blockedDuration"));

            verify(boardRepository).findById(boardId);
            verify(blockHistoryRepository).findByBoard(board);
            verifyNoInteractions(cardRepository);
        }
    }

    @Test
    void activeBlocksByBoard_BoardNotFound_ThrowsTaskboardException() {
        Long boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> blockHistoryService.activeBlocksByBoard(boardId));
        assertEquals("Quadro não encontrado com o ID: " + boardId, exception.getMessage());
        verify(boardRepository).findById(boardId);
        verifyNoInteractions(blockHistoryRepository, cardRepository);
    }

    @Test
    void activeBlocksByBoard_NoActiveBlocks_ReturnsEmptyList() {
        Long boardId = 1L;
        Board board = new Board();
        board.setId(boardId);

        Card card = new Card();
        card.setId(2L);
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(3L);
        taskStatus.setBoard(board);
        card.setTaskStatus(taskStatus);

        BlockHistory history = new BlockHistory();
        history.setId(4L);
        history.setCard(card);
        history.setBlockedDate(LocalDateTime.of(2025, 7, 28, 10, 0));
        history.setBlockedReason("Motivo 1");
        history.setUnblockedDate(LocalDateTime.of(2025, 7, 28, 12, 0));
        history.setUnblockedReason("Desbloqueado 1");

        List<BlockHistory> histories = Arrays.asList(history);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(blockHistoryRepository.findByBoard(board)).thenReturn(histories);

        List<Map<String, Object>> result = blockHistoryService.activeBlocksByBoard(boardId);

        assertTrue(result.isEmpty());
        verify(boardRepository).findById(boardId);
        verify(blockHistoryRepository).findByBoard(board);
        verifyNoInteractions(cardRepository);
    }

    @Test
    void activeBlocksByBoard_NoHistories_ReturnsEmptyList() {
        Long boardId = 1L;
        Board board = new Board();
        board.setId(boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(blockHistoryRepository.findByBoard(board)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = blockHistoryService.activeBlocksByBoard(boardId);

        assertTrue(result.isEmpty());
        verify(boardRepository).findById(boardId);
        verify(blockHistoryRepository).findByBoard(board);
        verifyNoInteractions(cardRepository);
    }
}
