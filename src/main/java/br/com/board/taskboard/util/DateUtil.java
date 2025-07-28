package br.com.board.taskboard.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateUtil {

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    /**
     * Calcula o tempo em horas entre duas datas, considerando a data atual se a data final for nula.
     * param start Data inicial (não nula).
     * param end Data final (pode ser nula).
     * return Tempo em horas como double (ex.: 24.5 para 24 horas e 30 minutos).
     */
    public static double calculateHours(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            return 0.0;
        }
        LocalDateTime endDate = end != null ? end : LocalDateTime.now(); // se end for nula, usa a data atual, se não, usa a data final fornecida
        Duration duration = Duration.between(start, endDate);
        return duration.toMinutes() / 60.0;
    }
}