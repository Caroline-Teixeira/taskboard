package br.com.board.taskboard.view;

import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.service.CardService;
import br.com.board.taskboard.util.ConsolePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.Scanner;

@Component
public class CardActionsMenu {

    private final CardService cardService;
    private final Scanner scanner;

    @Autowired
    public CardActionsMenu(CardService cardService, Scanner scanner) {
        this.cardService = cardService;
        this.scanner = scanner;
    }

    public void moveCard() {
        try {
            System.out.print("Digite o ID do cartão: ");
            Long cardId = scanner.nextLong();
            System.out.print("Digite o ID da coluna de destino: ");
            Long taskStatusId = scanner.nextLong();
            scanner.nextLine();
            cardService.moveCard(cardId, taskStatusId);
            ConsolePrinter.printSuccess("Cartão ID " + cardId + " movido para a coluna ID " + taskStatusId + ".");
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }

    public void blockCard() {
        try {
            System.out.print("Digite o ID do cartão: ");
            Long cardId = scanner.nextLong();
            scanner.nextLine();
            System.out.print("Digite o motivo do bloqueio: ");
            String reason = scanner.nextLine().trim();
            if (reason.isEmpty()) {
                ConsolePrinter.printError("Erro: O motivo do bloqueio não pode ser vazio.");
                return;
            }
            cardService.blockCard(cardId, reason);
            ConsolePrinter.printSuccess("Cartão ID " + cardId + " bloqueado com sucesso.");
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }

    public void unblockCard() {
        try {
            System.out.print("Digite o ID do cartão: ");
            Long cardId = scanner.nextLong();
            scanner.nextLine();
            System.out.print("Digite o motivo do desbloqueio: ");
            String reason = scanner.nextLine().trim();
            if (reason.isEmpty()) {
                ConsolePrinter.printError("Erro: O motivo do desbloqueio não pode ser vazio.");
                return;
            }
            cardService.unblockCard(cardId, reason);
            ConsolePrinter.printSuccess("Cartão ID " + cardId + " desbloqueado com sucesso.");
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }
}
