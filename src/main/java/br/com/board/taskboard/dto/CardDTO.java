package br.com.board.taskboard.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private boolean blocked;
    private String blockedReason;
    private String unblockedReason;
    private Long taskStatusId;
}