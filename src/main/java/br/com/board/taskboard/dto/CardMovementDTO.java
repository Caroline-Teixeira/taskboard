package br.com.board.taskboard.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardMovementDTO {
    private Long id;
    private Long cardId;
    private Long taskStatusId;
    private LocalDateTime entryDate;
    private LocalDateTime exitDate;
}