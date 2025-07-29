package br.com.board.taskboard.service;

import br.com.board.taskboard.dto.TaskStatusDTO;
import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Card;
import br.com.board.taskboard.model.Status;
import br.com.board.taskboard.model.TaskStatus;
import br.com.board.taskboard.repository.BoardRepository;
import br.com.board.taskboard.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskStatusServiceTest {

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private TaskStatusService taskStatusService;

    static class TaskStatusSaveAnswer implements Answer<TaskStatus> {
        @Override
        public TaskStatus answer(InvocationOnMock invocation) throws Throwable {
            TaskStatus saved = invocation.getArgument(0);
            if (saved.getStatus() == Status.INICIAL) {
                saved.setId(1L);
            } else if (saved.getStatus() == Status.PENDENTE) {
                saved.setId(2L);
            } else if (saved.getStatus() == Status.FINAL) {
                saved.setId(3L);
            } else {
                saved.setId(4L);
            }
            return saved;
        }
    }

    @Test
    void createMandatoryColumns_ValidBoard_CreatesColumns() {
        Board board = new Board();
        board.setId(1L);

        when(taskStatusRepository.findByBoardAndStatus(board, Status.INICIAL)).thenReturn(Optional.empty());
        when(taskStatusRepository.findByBoardAndStatus(board, Status.FINAL)).thenReturn(Optional.empty());
        when(taskStatusRepository.findByBoardAndStatus(board, Status.CANCELADA)).thenReturn(Optional.empty());
        when(taskStatusRepository.save(any(TaskStatus.class))).thenAnswer(new TaskStatusSaveAnswer());

        taskStatusService.createMandatoryColumns(board);

        verify(taskStatusRepository).findByBoardAndStatus(board, Status.INICIAL);
        verify(taskStatusRepository).findByBoardAndStatus(board, Status.FINAL);
        verify(taskStatusRepository).findByBoardAndStatus(board, Status.CANCELADA);
        verify(taskStatusRepository, times(4)).save(any(TaskStatus.class));

        verify(taskStatusRepository).save(argThat(taskStatus ->
            taskStatus.getName().equals("A Fazer") &&
            taskStatus.getStatus() == Status.INICIAL &&
            taskStatus.getPriority() == 1 &&
            taskStatus.getBoard().equals(board)
        ));
        verify(taskStatusRepository).save(argThat(taskStatus ->
            taskStatus.getName().equals("Em progresso") &&
            taskStatus.getStatus() == Status.PENDENTE &&
            taskStatus.getPriority() == 2 &&
            taskStatus.getBoard().equals(board)
        ));
        verify(taskStatusRepository).save(argThat(taskStatus ->
            taskStatus.getName().equals("Concluído") &&
            taskStatus.getStatus() == Status.FINAL &&
            taskStatus.getPriority() == 3 &&
            taskStatus.getBoard().equals(board)
        ));
        verify(taskStatusRepository).save(argThat(taskStatus ->
            taskStatus.getName().equals("Cancelado") &&
            taskStatus.getStatus() == Status.CANCELADA &&
            taskStatus.getPriority() == 4 &&
            taskStatus.getBoard().equals(board)
        ));
    }

    @Test
    void createMandatoryColumns_NullBoard_ThrowsTaskboardException() {
        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> taskStatusService.createMandatoryColumns(null));
        assertEquals("O quadro não pode ser nulo ou sem ID.", exception.getMessage());
        verifyNoInteractions(taskStatusRepository);
    }

    @Test
    void createMandatoryColumns_BoardWithoutId_ThrowsTaskboardException() {
        Board board = new Board();
        board.setId(null);

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> taskStatusService.createMandatoryColumns(board));
        assertEquals("O quadro não pode ser nulo ou sem ID.", exception.getMessage());
        verifyNoInteractions(taskStatusRepository);
    }

    @Test
    void createMandatoryColumns_InitialColumnExists_ThrowsTaskboardException() {
        Board board = new Board();
        board.setId(1L);
        TaskStatus initialColumn = new TaskStatus();
        initialColumn.setStatus(Status.INICIAL);

        when(taskStatusRepository.findByBoardAndStatus(board, Status.INICIAL)).thenReturn(Optional.of(initialColumn));

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> taskStatusService.createMandatoryColumns(board));
        assertEquals("O quadro já possui coluna Inicial.", exception.getMessage());
        verify(taskStatusRepository).findByBoardAndStatus(board, Status.INICIAL);
        verifyNoMoreInteractions(taskStatusRepository);
    }

    @Test
    void createMandatoryColumns_FinalColumnExists_ThrowsTaskboardException() {
        Board board = new Board();
        board.setId(1L);
        TaskStatus finalColumn = new TaskStatus();
        finalColumn.setStatus(Status.FINAL);

        when(taskStatusRepository.findByBoardAndStatus(board, Status.INICIAL)).thenReturn(Optional.empty());
        when(taskStatusRepository.findByBoardAndStatus(board, Status.FINAL)).thenReturn(Optional.of(finalColumn));

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> taskStatusService.createMandatoryColumns(board));
        assertEquals("O quadro já possui coluna Final.", exception.getMessage());
        verify(taskStatusRepository).findByBoardAndStatus(board, Status.INICIAL);
        verify(taskStatusRepository).findByBoardAndStatus(board, Status.FINAL);
        verifyNoMoreInteractions(taskStatusRepository);
    }

    @Test
    void createMandatoryColumns_CancelledColumnExists_ThrowsTaskboardException() {
        Board board = new Board();
        board.setId(1L);
        TaskStatus cancelledColumn = new TaskStatus();
        cancelledColumn.setStatus(Status.CANCELADA);

        when(taskStatusRepository.findByBoardAndStatus(board, Status.INICIAL)).thenReturn(Optional.empty());
        when(taskStatusRepository.findByBoardAndStatus(board, Status.FINAL)).thenReturn(Optional.empty());
        when(taskStatusRepository.findByBoardAndStatus(board, Status.CANCELADA)).thenReturn(Optional.of(cancelledColumn));

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> taskStatusService.createMandatoryColumns(board));
        assertEquals("O quadro já possui coluna Cancelada.", exception.getMessage());
        verify(taskStatusRepository).findByBoardAndStatus(board, Status.INICIAL);
        verify(taskStatusRepository).findByBoardAndStatus(board, Status.FINAL);
        verify(taskStatusRepository).findByBoardAndStatus(board, Status.CANCELADA);
        verifyNoMoreInteractions(taskStatusRepository);
    }

    @Test
    void listColumns_ValidBoardId_ReturnsTaskStatusDTOList() {
        Long boardId = 1L;
        Board board = new Board();
        board.setId(boardId);

        TaskStatus taskStatus1 = new TaskStatus();
        taskStatus1.setId(2L);
        taskStatus1.setName("A Fazer");
        taskStatus1.setStatus(Status.INICIAL);
        taskStatus1.setPriority(1);
        taskStatus1.setBoard(board);
        Card card1 = new Card();
        card1.setId(3L);
        taskStatus1.setCards(Arrays.asList(card1));

        TaskStatus taskStatus2 = new TaskStatus();
        taskStatus2.setId(4L);
        taskStatus2.setName("Em progresso");
        taskStatus2.setStatus(Status.PENDENTE);
        taskStatus2.setPriority(2);
        taskStatus2.setBoard(board);
        Card card2 = new Card();
        card2.setId(5L);
        taskStatus2.setCards(Arrays.asList(card2));

        List<TaskStatus> taskStatuses = Arrays.asList(taskStatus1, taskStatus2);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(taskStatusRepository.findByBoardOrderByPriority(board)).thenReturn(taskStatuses);

        List<TaskStatusDTO> result = taskStatusService.listColumns(boardId);

        assertEquals(2, result.size());
        TaskStatusDTO dto1 = result.get(0);
        assertEquals(2L, dto1.getId());
        assertEquals("A Fazer", dto1.getName());
        assertEquals(1, dto1.getPriority());
        assertEquals(Status.INICIAL, dto1.getStatus());
        assertEquals(boardId, dto1.getBoardId());
        assertEquals(Arrays.asList(3L), dto1.getCardIds());

        TaskStatusDTO dto2 = result.get(1);
        assertEquals(4L, dto2.getId());
        assertEquals("Em progresso", dto2.getName());
        assertEquals(2, dto2.getPriority());
        assertEquals(Status.PENDENTE, dto2.getStatus());
        assertEquals(boardId, dto2.getBoardId());
        assertEquals(Arrays.asList(5L), dto2.getCardIds());

        verify(boardRepository).findById(boardId);
        verify(taskStatusRepository).findByBoardOrderByPriority(board);
    }

    @Test
    void listColumns_BoardNotFound_ThrowsTaskboardException() {
        Long boardId = 1L;

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> taskStatusService.listColumns(boardId));
        assertEquals("Quadro não encontrado com o ID: " + boardId, exception.getMessage());
        verify(boardRepository).findById(boardId);
        verifyNoInteractions(taskStatusRepository);
    }

    @Test
    void listColumns_EmptyColumns_ReturnsEmptyList() {
        Long boardId = 1L;
        Board board = new Board();
        board.setId(boardId);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(taskStatusRepository.findByBoardOrderByPriority(board)).thenReturn(Collections.emptyList());

        List<TaskStatusDTO> result = taskStatusService.listColumns(boardId);

        assertTrue(result.isEmpty());
        verify(boardRepository).findById(boardId);
        verify(taskStatusRepository).findByBoardOrderByPriority(board);
    }
}