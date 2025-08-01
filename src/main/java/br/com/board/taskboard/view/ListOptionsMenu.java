package br.com.board.taskboard.view;

import br.com.board.taskboard.dto.CardMovementDTO;
import br.com.board.taskboard.dto.TaskStatusDTO;
import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.service.BlockHistoryService;
import br.com.board.taskboard.service.CardMovementService;
import br.com.board.taskboard.service.TaskStatusService;
import br.com.board.taskboard.util.ConsolePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class ListOptionsMenu {

    private final TaskStatusService taskStatusService;
    private final CardMovementService cardMovementService;
    private final BlockHistoryService blockHistoryService;
    private final Scanner scanner;

    @Autowired
    public ListOptionsMenu(TaskStatusService taskStatusService, CardMovementService cardMovementService,
                          BlockHistoryService blockHistoryService, Scanner scanner) {
        this.taskStatusService = taskStatusService;
        this.cardMovementService = cardMovementService;
        this.blockHistoryService = blockHistoryService;
        this.scanner = scanner;
    }

    public void listColumns() {
        try {
            System.out.print("Digite o ID do quadro: ");
            Long boardId = scanner.nextLong();
            scanner.nextLine();
            List<TaskStatusDTO> columns = taskStatusService.listColumns(boardId);
            if (columns.isEmpty()) {
                ConsolePrinter.printInfo("Nenhuma coluna encontrada para o quadro ID " + boardId + ".");
                return;
            }
            ConsolePrinter.printInfo("Colunas do quadro ID " + boardId + ":");
            for (TaskStatusDTO column : columns) {
                System.out.println("ID: " + column.getId() + ", Nome: " + column.getName() +
                        ", Prioridade: " + column.getPriority() + ", Status: " + column.getStatus() +
                        ", Cartões: " + (column.getCardIds().isEmpty() ? "Nenhum" : column.getCardIds()));
            }
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }

    public void listCardMovements() {
        try {
            System.out.print("Digite o ID do cartão: ");
            Long cardId = scanner.nextLong();
            scanner.nextLine();
            List<CardMovementDTO> movements = cardMovementService.listMovements(cardId);
            if (movements.isEmpty()) {
                ConsolePrinter.printInfo("Nenhuma movimentação encontrada para o cartão ID " + cardId + ".");
                return;
            }
            ConsolePrinter.printInfo("Movimentações do cartão ID " + cardId + ":");
            for (CardMovementDTO movement : movements) {
                System.out.println("ID: " + movement.getId() + ", Coluna ID: " + movement.getTaskStatusId() +
                        ", Entrada: " + movement.getEntryDate() +
                        ", Saída: " + (movement.getExitDate() != null ? movement.getExitDate() : "Ainda na coluna"));
            }
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }

    public void listBlockHistory() {
        try {
            System.out.print("Digite o ID do quadro: ");
            Long boardId = scanner.nextLong();
            System.out.print("Digite o ID do cartão (ou 0 para todos os cartões): ");
            Long cardId = scanner.nextLong();
            scanner.nextLine();
            List<Map<String, Object>> blockHistories = blockHistoryService.cardBlockHistory(boardId, cardId == 0 ? null : cardId);
            if (blockHistories.isEmpty()) {
                ConsolePrinter.printInfo("Nenhum histórico de bloqueio encontrado.");
                return;
            }

            // map para exibir o histórico de bloqueios
            ConsolePrinter.printInfo("Histórico de bloqueios:");
            for (Map<String, Object> history : blockHistories) {
                System.out.println("ID: " + history.get("id") + ", Cartão ID: " + history.get("cardId") +
                        ", Data de Bloqueio: " + history.get("blockedDate") +
                        ", Motivo: " + history.get("blockedReason") +
                        ", Data de Desbloqueio: " + (history.get("unblockedDate") != null ? history.get("unblockedDate") : "Ainda bloqueado") +
                        ", Motivo de Desbloqueio: " + (history.get("unblockedReason") != null ? history.get("unblockedReason") : "N/A") +
                        ", Duração (horas): " + history.get("blockedDuration"));
            }
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }

    public void listActiveBlocks() {
        try {
            System.out.print("Digite o ID do quadro: ");
            Long boardId = scanner.nextLong();
            scanner.nextLine();
            List<Map<String, Object>> activeBlocks = blockHistoryService.activeBlocksByBoard(boardId);
            if (activeBlocks.isEmpty()) {
                ConsolePrinter.printInfo("Nenhum bloqueio ativo encontrado para o quadro ID " + boardId + ".");
                return;
            }
            ConsolePrinter.printInfo("Bloqueios ativos do quadro ID " + boardId + ":");
            for (Map<String, Object> block : activeBlocks) {
                System.out.println("ID: " + block.get("id") + ", Cartão ID: " + block.get("cardId") +
                        ", Data de Bloqueio: " + block.get("blockedDate") +
                        ", Motivo: " + block.get("blockedReason"));
            }
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }
}
