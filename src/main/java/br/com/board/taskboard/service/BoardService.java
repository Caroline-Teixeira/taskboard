package br.com.board.taskboard.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import br.com.board.taskboard.dto.BoardDTO;
import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.model.Board;

import br.com.board.taskboard.repository.BoardRepository;
import jakarta.transaction.Transactional;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final TaskStatusService taskStatusService;

    @Autowired
    public BoardService(BoardRepository boardRepository, TaskStatusService taskStatusService) {
        this.boardRepository = boardRepository;
        this.taskStatusService = taskStatusService;
    }

    // MÉTODOS
    // novo quadro
    @Transactional
    public BoardDTO createBoard(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new TaskboardException("O nome do quadro não pode ser vazio.");
        }

        // cria o Board
        Board board = new Board();
        board.setName(name);
        board.setCreatedAt(LocalDateTime.now());
        board.setUpdatedAt(LocalDateTime.now());

        board = boardRepository.save(board);
        
        // Colunas obrigatórias
        taskStatusService.createMandatoryColumns(board);

        // acessa board e converte para DTO
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(board.getId());
        boardDTO.setName(board.getName());
        boardDTO.setCreatedAt(board.getCreatedAt());
        boardDTO.setUpdatedAt(board.getUpdatedAt());
        boardDTO.setTaskStatusIds(board.getTaskStatuses().stream()
                .map(taskStatus -> taskStatus.getId())
                .collect(Collectors.toList()));
        
        return boardDTO;
    }

    // lista todos os quadros
    public List<BoardDTO> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(board -> { // mapeia o DTO
                    BoardDTO boardDTO = new BoardDTO();
                    boardDTO.setId(board.getId());
                    boardDTO.setName(board.getName());
                    boardDTO.setCreatedAt(board.getCreatedAt());
                    boardDTO.setUpdatedAt(board.getUpdatedAt());
                    boardDTO.setTaskStatusIds(board.getTaskStatuses().stream()
                            .map(taskStatus -> taskStatus.getId())
                            .collect(Collectors.toList()));
                    return boardDTO;
                })
                .collect(Collectors.toList());
    }

    // busca um quadro por ID
    public BoardDTO getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new TaskboardException("Quadro não encontrado com o ID: " + id));

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(board.getId());
        boardDTO.setName(board.getName());
        boardDTO.setCreatedAt(board.getCreatedAt());
        boardDTO.setUpdatedAt(board.getUpdatedAt());
        boardDTO.setTaskStatusIds(board.getTaskStatuses().stream()
                .map(taskStatus -> taskStatus.getId()) // Mapeia para obter os IDs das colunas
                .collect(Collectors.toList())); // faz uma lista com os IDs

        return boardDTO;
    }
}
