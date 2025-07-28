package br.com.board.taskboard.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlockHistoryDTO {
    private Long id;
    private Long cardId;
    private LocalDateTime blockDate;
    private String blockedReason;
    private LocalDateTime unblockedDate;
    private String unblockedReason;
}