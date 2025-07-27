package br.com.board.taskboard.dto;

import br.com.board.taskboard.model.Status;
import lombok.Data;

import java.util.List;

@Data
public class TaskStatusDTO {
    private Long id;
    private String name;
    private int priority;
    private Status status;
    private Long boardId;
    private List<Long> cardIds;
}