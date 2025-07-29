package br.com.board.taskboard.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BlockHistoryDTO {

  private Long id;
  private Long cardId;
  private LocalDateTime blockedDate;
  private String blockedReason;
  private LocalDateTime unblockedDate;
  private String unblockedReason;
}
