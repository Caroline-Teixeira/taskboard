package br.com.board.taskboard.service;

import br.com.board.taskboard.dto.CardMovementDTO;
import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Card;
import br.com.board.taskboard.model.CardMovement;
import br.com.board.taskboard.model.TaskStatus;
import br.com.board.taskboard.repository.BoardRepository;
import br.com.board.taskboard.repository.CardRepository;
import br.com.board.taskboard.repository.CardMovementRepository;
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
class CardMovementServiceTest {

    @Mock
    private CardMovementRepository cardMovementRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardMovementService cardMovementService;

    @Test
    void cardTimeInColumn_ValidBoardId_ReturnsMovementList() {
        Long boardId = 1L;
        Board board = new Board();
        board.setId(boardId);

        Card card = new Card();
        card.setId(2L);
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(3L);

        CardMovement movement1 = new CardMovement();
        movement1.setId(4L);
        movement1.setCard(card);
        movement1.setTaskStatus(taskStatus);
        LocalDateTime entryDate1 = LocalDateTime.of(2025, 7, 28, 10, 0);
        LocalDateTime exitDate1 = LocalDateTime.of(2025, 7, 28, 12, 0);
        movement1.setEntryDate(entryDate1);
        movement1.setExitDate(exitDate1);

        CardMovement movement2 = new CardMovement();
        movement2.setId(5L);
        movement2.setCard(card);
        movement2.setTaskStatus(taskStatus);
        LocalDateTime entryDate2 = LocalDateTime.of(2025, 7, 28, 12, 0);
        movement2.setEntryDate(entryDate2);
        movement2.setExitDate(null);

        List<CardMovement> movements = Arrays.asList(movement1, movement2);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(cardMovementRepository.findByBoard(board)).thenReturn(movements);
        try (MockedStatic<DateUtil> dateUtil = mockStatic(DateUtil.class)) {
            dateUtil.when(() -> DateUtil.calculateHours(entryDate1, exitDate1)).thenReturn(2.0);
            dateUtil.when(() -> DateUtil.calculateHours(entryDate2, null)).thenReturn(0.0);

            List<Map<String, Object>> result = cardMovementService.cardTimeInColumn(boardId);

            assertEquals(2, result.size());

            Map<String, Object> result1 = result.get(0);
            assertEquals(4L, result1.get("id"));
            assertEquals(2L, result1.get("cardId"));
            assertEquals(3L, result1.get("taskStatusId"));
            assertEquals(entryDate1, result1.get("entryDate"));
            assertEquals(exitDate1, result1.get("exitDate"));
            assertEquals(2.0, result1.get("timeInHours"));

            Map<String, Object> result2 = result.get(1);
            assertEquals(5L, result2.get("id"));
            assertEquals(2L, result2.get("cardId"));
            assertEquals(3L, result2.get("taskStatusId"));
            assertEquals(entryDate2, result2.get("entryDate"));
            assertNull(result2.get("exitDate"));
            assertEquals(0.0, result2.get("timeInHours"));

            verify(boardRepository).findById(boardId);
            verify(cardMovementRepository).findByBoard(board);
        }
    }

    @Test
    void cardTimeInColumn_BoardNotFound_ThrowsTaskboardException() {
        Long boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardMovementService.cardTimeInColumn(boardId));
        assertEquals("Quadro não encontrado com o ID: " + boardId, exception.getMessage());
        verify(boardRepository).findById(boardId);
        verifyNoInteractions(cardMovementRepository);
    }

    @Test
    void cardTimeInColumn_NoMovements_ReturnsEmptyList() {
        Long boardId = 1L;
        Board board = new Board();
        board.setId(boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(cardMovementRepository.findByBoard(board)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = cardMovementService.cardTimeInColumn(boardId);

        assertTrue(result.isEmpty());
        verify(boardRepository).findById(boardId);
        verify(cardMovementRepository).findByBoard(board);
    }

    @Test
    void listMovements_ValidCardId_ReturnsMovementDTOList() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(2L);

        CardMovement movement1 = new CardMovement();
        movement1.setId(3L);
        movement1.setCard(card);
        movement1.setTaskStatus(taskStatus);
        LocalDateTime entryDate1 = LocalDateTime.of(2025, 7, 28, 10, 0);
        LocalDateTime exitDate1 = LocalDateTime.of(2025, 7, 28, 12, 0);
        movement1.setEntryDate(entryDate1);
        movement1.setExitDate(exitDate1);

        CardMovement movement2 = new CardMovement();
        movement2.setId(4L);
        movement2.setCard(card);
        movement2.setTaskStatus(taskStatus);
        LocalDateTime entryDate2 = LocalDateTime.of(2025, 7, 28, 12, 0);
        movement2.setEntryDate(entryDate2);
        movement2.setExitDate(null);

        List<CardMovement> movements = Arrays.asList(movement1, movement2);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardMovementRepository.findByCard(card)).thenReturn(movements);

        List<CardMovementDTO> result = cardMovementService.listMovements(cardId);

        assertEquals(2, result.size());

        CardMovementDTO dto1 = result.get(0);
        assertEquals(3L, dto1.getId());
        assertEquals(cardId, dto1.getCardId());
        assertEquals(2L, dto1.getTaskStatusId());
        assertEquals(entryDate1, dto1.getEntryDate());
        assertEquals(exitDate1, dto1.getExitDate());

        CardMovementDTO dto2 = result.get(1);
        assertEquals(4L, dto2.getId());
        assertEquals(cardId, dto2.getCardId());
        assertEquals(2L, dto2.getTaskStatusId());
        assertEquals(entryDate2, dto2.getEntryDate());
        assertNull(dto2.getExitDate());

        verify(cardRepository).findById(cardId);
        verify(cardMovementRepository).findByCard(card);
    }

    @Test
    void listMovements_CardNotFound_ThrowsTaskboardException() {
        Long cardId = 1L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> cardMovementService.listMovements(cardId));
        assertEquals("Cartão não encontrado com o ID: " + cardId, exception.getMessage());
        verify(cardRepository).findById(cardId);
        verifyNoInteractions(cardMovementRepository);
    }

    @Test
    void listMovements_NoMovements_ReturnsEmptyList() {
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardMovementRepository.findByCard(card)).thenReturn(Collections.emptyList());

        List<CardMovementDTO> result = cardMovementService.listMovements(cardId);

        assertTrue(result.isEmpty());
        verify(cardRepository).findById(cardId);
        verify(cardMovementRepository).findByCard(card);
    }
}