package br.com.board.taskboard.service;

import br.com.board.taskboard.dto.BoardDTO;
import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.TaskStatus;
import br.com.board.taskboard.repository.BoardRepository;
import br.com.board.taskboard.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private TaskStatusService taskStatusService;

    @InjectMocks
    private BoardService boardService;

    @Test
    void createBoard_ValidName_ReturnsBoardDTO() {
        String name = "Quadro Teste";
        Board board = new Board();
        board.setId(1L);
        board.setName(name);
        LocalDateTime now = LocalDateTime.of(2025, 7, 28, 18, 0);
        board.setCreatedAt(now);
        board.setUpdatedAt(now);
        board.setTaskStatuses(Collections.emptyList()); // Inicializa taskStatuses como lista vazia

        when(boardRepository.save(any(Board.class))).thenReturn(board);
        try (MockedStatic<DateUtil> dateUtil = Mockito.mockStatic(DateUtil.class)) {
            dateUtil.when(() -> DateUtil.now()).thenReturn(now); 
            BoardDTO result = boardService.createBoard(name);

            assertEquals(1L, result.getId());
            assertEquals(name, result.getName());
            assertEquals(now, result.getCreatedAt());
            assertEquals(now, result.getUpdatedAt());
            assertNotNull(result.getTaskStatusIds());
            assertTrue(result.getTaskStatusIds().isEmpty());
            verify(boardRepository).save(any(Board.class));
            verify(taskStatusService).createMandatoryColumns(board);
        }
    }

    @Test
    void createBoard_EmptyName_ThrowsTaskboardException() {
        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> boardService.createBoard(""));
        assertEquals("O nome do quadro não pode ser vazio.", exception.getMessage());
        verifyNoInteractions(boardRepository, taskStatusService);
    }

    @Test
    void getAllBoards_ReturnsListOfBoardDTOs() {
        Board board = new Board();
        board.setId(1L);
        board.setName("Quadro Teste");
        LocalDateTime now = LocalDateTime.of(2025, 7, 28, 18, 0);
        board.setCreatedAt(now);
        board.setUpdatedAt(now);
        board.setTaskStatuses(List.of(new TaskStatus()));

        when(boardRepository.findAll()).thenReturn(List.of(board));

        List<BoardDTO> result = boardService.getAllBoards();

        assertEquals(1, result.size());
        BoardDTO boardDTO = result.get(0);
        assertEquals(1L, boardDTO.getId());
        assertEquals("Quadro Teste", boardDTO.getName());
        assertEquals(now, boardDTO.getCreatedAt());
        assertEquals(now, boardDTO.getUpdatedAt());
        assertEquals(1, boardDTO.getTaskStatusIds().size());
        verify(boardRepository).findAll();
    }

    @Test
    void getBoardById_ValidId_ReturnsBoardDTO() {
        Long boardId = 1L;
        Board board = new Board();
        board.setId(boardId);
        board.setName("Quadro Teste");
        LocalDateTime now = LocalDateTime.of(2025, 7, 28, 18, 0);
        board.setCreatedAt(now);
        board.setUpdatedAt(now);
        board.setTaskStatuses(List.of(new TaskStatus()));

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        BoardDTO result = boardService.getBoardById(boardId);

        assertEquals(boardId, result.getId());
        assertEquals("Quadro Teste", result.getName());
        assertEquals(now, result.getCreatedAt());
        assertEquals(now, result.getUpdatedAt());
        assertEquals(1, result.getTaskStatusIds().size());
        verify(boardRepository).findById(boardId);
    }

    @Test
    void getBoardById_InvalidId_ThrowsTaskboardException() {
        Long boardId = 1L;
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        TaskboardException exception = assertThrows(TaskboardException.class,
                () -> boardService.getBoardById(boardId));
        assertEquals("Quadro não encontrado com o ID: " + boardId, exception.getMessage());
        verify(boardRepository).findById(boardId);
    }
}