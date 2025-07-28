package br.com.board.taskboard.service;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.board.taskboard.dto.TaskStatusDTO;
import br.com.board.taskboard.model.Board;
import br.com.board.taskboard.model.Status;
import br.com.board.taskboard.model.TaskStatus;
import br.com.board.taskboard.repository.BoardRepository;
import br.com.board.taskboard.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;

@Service
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public TaskStatusService(TaskStatusRepository taskStatusRepository, BoardRepository boardRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.boardRepository = boardRepository;
    }

    // Cria as colunas obrigatórias para um quadro
    @Transactional
    public void createMandatoryColumns(Board board) {
        if (board == null || board.getId() == null) {
            throw new IllegalArgumentException("O quadro não pode ser nulo ou sem ID.");
        }

        // Verifica se as colunas obrigatórias já existem
        if (taskStatusRepository.findByBoardAndStatus(board, Status.INICIAL).isPresent()){
            throw new IllegalArgumentException("O quadro já possui coluna Inicial.");
        }

        if (taskStatusRepository.findByBoardAndStatus(board, Status.FINAL).isPresent()){
            throw new IllegalArgumentException("O quadro já possui coluna Final.");
        }

        if (taskStatusRepository.findByBoardAndStatus(board, Status.CANCELADA).isPresent()){
            throw new IllegalArgumentException("O quadro já possui coluna Cancelada.");    
        }

        // Cria as colunas Iniciais
        TaskStatus initialColumn = new TaskStatus();
        initialColumn.setName("A Fazer");
        initialColumn.setStatus(Status.INICIAL);
        initialColumn.setBoard(board);
        initialColumn.setPriority(1); // Define a prioridade como 1
        taskStatusRepository.save(initialColumn);

        // Cria as colunas pendentes
        TaskStatus pendingColumn = new TaskStatus();
        pendingColumn.setName("Em progresso");
        pendingColumn.setStatus(Status.PENDENTE);
        pendingColumn.setBoard(board);
        pendingColumn.setPriority(2); // Define a prioridade como 2
        taskStatusRepository.save(pendingColumn);

        // Cria as colunas finais
        TaskStatus finalColumn = new TaskStatus();
        finalColumn.setName("Concluído");
        finalColumn.setStatus(Status.FINAL);
        finalColumn.setBoard(board);
        finalColumn.setPriority(3); // Define a prioridade como 3
        taskStatusRepository.save(finalColumn);

        // Cria as colunas canceladas
        TaskStatus cancelledColumn = new TaskStatus();
        cancelledColumn.setName("Cancelado");
        cancelledColumn.setStatus(Status.CANCELADA);
        cancelledColumn.setBoard(board);
        cancelledColumn.setPriority(4); // Define a prioridade como 4
        taskStatusRepository.save(cancelledColumn);

    }


    // Lista as colunas de um quadro
    public List<TaskStatusDTO> listColumns(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Quadro não encontrado com o ID: " + boardId));

        List<TaskStatus> taskStatuses = taskStatusRepository.findByBoardOrderByPriority(board);

        return taskStatuses.stream()
                .map(taskStatus -> {
                    TaskStatusDTO taskDTO = new TaskStatusDTO();
                    taskDTO.setId(taskStatus.getId());
                    taskDTO.setName(taskStatus.getName());
                    taskDTO.setPriority(taskStatus.getPriority());
                    taskDTO.setStatus(taskStatus.getStatus());
                    taskDTO.setBoardId(board.getId());
                    taskDTO.setCardIds(taskStatus.getCards().stream()
                            .map(card -> card.getId())
                            .collect(Collectors.toList()));
                    return taskDTO;
                })
                .collect(Collectors.toList());
        
    }

}
