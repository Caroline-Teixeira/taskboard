package br.com.board.taskboard.exception;

public class TaskboardException extends RuntimeException {

    public TaskboardException(String message) {
        super(message);
    }

    public TaskboardException(String message, Throwable cause) {
        super(message, cause);
    }
}