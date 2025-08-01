package br.com.board.taskboard.view;

import br.com.board.taskboard.dto.BoardDTO;
import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.service.BoardService;
import br.com.board.taskboard.util.ConsolePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class BoardMenu {

    private final BoardService boardService;
    private final Scanner scanner;

    @Autowired
    public BoardMenu(BoardService boardService, Scanner scanner) {
        this.boardService = boardService;
        this.scanner = scanner;
    }

    public void createBoard() {
        try {
            System.out.print("Digite o nome do quadro: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                ConsolePrinter.printError("Erro: O nome do quadro não pode ser vazio.");
                return;
            }
            BoardDTO boardDTO = boardService.createBoard(name);
            ConsolePrinter.printSuccess("Quadro '" + name + "' criado com sucesso. ID: " + boardDTO.getId());
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }
}