package br.com.board.taskboard.view;

import br.com.board.taskboard.dto.CardDTO;
import br.com.board.taskboard.dto.CardMovementDTO;
import br.com.board.taskboard.dto.TaskStatusDTO;
import br.com.board.taskboard.exception.TaskboardException;
import br.com.board.taskboard.model.Board;

import br.com.board.taskboard.service.BlockHistoryService;
import br.com.board.taskboard.service.BoardService;
import br.com.board.taskboard.service.CardMovementService;
import br.com.board.taskboard.service.CardService;
import br.com.board.taskboard.service.TaskStatusService;
import br.com.board.taskboard.util.ConsolePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class Menu {

    private final CardService cardService;
    private final TaskStatusService taskStatusService;
    private final CardMovementService cardMovementService;
    private final BlockHistoryService blockHistoryService;
    private final BoardService boardService;
    private final Scanner scanner;

    @Autowired
    public Menu(CardService cardService, TaskStatusService taskStatusService,
                CardMovementService cardMovementService, BlockHistoryService blockHistoryService, BoardService boardService) {
        this.cardService = cardService;
        this.taskStatusService = taskStatusService;
        this.cardMovementService = cardMovementService;
        this.blockHistoryService = blockHistoryService;
        this.boardService = boardService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("=== Menu do Taskboard ===");
            System.out.println("1. Criar Quadro");
            System.out.println("2. Criar Colunas Obrigatórias");
            System.out.println("3. Adicionar Cartão");
            System.out.println("4. Mover Cartão");
            System.out.println("5. Bloquear Cartão");
            System.out.println("6. Desbloquear Cartão");
            System.out.println("7. Listar Colunas do Quadro");
            System.out.println("8. Listar Movimentações do Cartão");
            System.out.println("9. Listar Histórico de Bloqueios");
            System.out.println("10. Listar Bloqueios Ativos");
            System.out.println("11. Sair");
            System.out.println("=========================");
            System.out.print("Escolha uma opção (1-11): ");

            try {
                String input = scanner.nextLine().trim();
                int option = Integer.parseInt(input);
                switch (option) {
                    case 1 -> createBoard();
                    case 2 -> createMandatoryColumns();
                    case 3 -> addCard();
                    case 4 -> moveCard();
                    case 5 -> blockCard();
                    case 6 -> unblockCard();
                    case 7 -> listColumns();
                    case 8 -> listCardMovements();
                    case 9 -> listBlockHistory();
                    case 10 -> listActiveBlocks();
                    case 11 -> {
                        ConsolePrinter.printWarning("Saindo...");
                        scanner.close();
                        return;
                    }
                    default -> ConsolePrinter.printError("Opção inválida! Tente novamente.");
                }
            } catch (NumberFormatException e) {
                ConsolePrinter.printError("Erro: A entrada deve ser um número válido.");
            }
        }
    }

    private void createBoard() {
        try {
            System.out.print("Digite o nome do quadro: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                ConsolePrinter.printError("Erro: O nome do quadro não pode ser vazio.");
                return;
            }
            Board board = new Board();
            board.setName(name);
            boardService.createBoard(name);
            ConsolePrinter.printSuccess("Quadro '" + name + "' criado com sucesso.");
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }

    private void createMandatoryColumns() {
        try {
            System.out.print("Digite o ID do quadro: ");
            Long boardId = scanner.nextLong();
            scanner.nextLine();
            Board board = new Board();
            board.setId(boardId);
            taskStatusService.createMandatoryColumns(board);
            ConsolePrinter.printSuccess("Colunas obrigatórias criadas para o quadro ID " + boardId + ".");
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }

    private void addCard() {
        try {
            System.out.print("Digite o ID do quadro: ");
            Long boardId = scanner.nextLong();
            System.out.print("Digite o ID da coluna: ");
            Long taskStatusId = scanner.nextLong();
            System.out.print("Digite o título do cartão: ");
            scanner.nextLine();
            String title = scanner.nextLine().trim();
            System.out.print("Digite a descrição do cartão: ");
            String description = scanner.nextLine().trim();
            if (title.isEmpty()) {
                ConsolePrinter.printError("Erro: O título do cartão não pode ser vazio.");
                return;
            }
            CardDTO cardDTO = new CardDTO();
            cardDTO.setTitle(title);
            cardDTO.setDescription(description);
            cardDTO.setId(boardId);
            cardDTO.setTaskStatusId(taskStatusId);
            cardService.createCard(boardId, title, description);
            ConsolePrinter.printSuccess("Cartão '" + title + "' adicionado com sucesso.");
        } catch (InputMismatchException e) {
            ConsolePrinter.printError("Erro: Digite apenas números válidos!");
            scanner.nextLine();
        } catch (TaskboardException e) {
            ConsolePrinter.printError("Erro: " + e.getMessage());
        }
    }

    private void moveCard() {
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

    private void blockCard() {
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

    private void unblockCard() {
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

    private void listColumns() {
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

    private void listCardMovements() {
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

    private void listBlockHistory() {
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

    private void listActiveBlocks() {
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