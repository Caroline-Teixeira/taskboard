package br.com.board.taskboard.view;

import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.service.CardService;
import br.com.board.taskboard.util.ConsolePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.Scanner;

@Component
public class CardMenu {

    private final CardService cardService;
    private final Scanner scanner;

    @Autowired
    public CardMenu(CardService cardService, Scanner scanner) {
        this.cardService = cardService;
        this.scanner = scanner;
    }

    public void addCard() {
        try {
            System.out.print("Digite o ID do quadro: ");
            Long boardId = scanner.nextLong();
            scanner.nextLine();
            System.out.print("Digite o título do cartão: ");
            String title = scanner.nextLine().trim();
            System.out.print("Digite a descrição do cartão: ");
            String description = scanner.nextLine().trim();
            if (title.isEmpty()) {
                ConsolePrinter.printError("Erro: O título do cartão não pode ser vazio.");
                return;
            }
            cardService.createCard(boardId, title, description);
            ConsolePrinter.printSuccess("Cartão '" + title + "' adicionado com sucesso à coluna Inicial.");
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }


    public void deleteCard() {
        try {
            System.out.print("Digite o ID do cartão: ");
            Long cardId = scanner.nextLong();
            scanner.nextLine();
            cardService.deleteCard(cardId);
            ConsolePrinter.printSuccess("Cartão ID " + cardId + " deletado com sucesso.");
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }
}
