package br.com.board.taskboard.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateUtil {

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    public static double calculateHours(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            return 0.0;
        }
        LocalDateTime endDate = end != null ? end : LocalDateTime.now(); // se end for nula, usa a data atual, se não, usa a data final fornecida
        Duration duration = Duration.between(start, endDate);
        return duration.toMinutes() / 60.0;
    }
}