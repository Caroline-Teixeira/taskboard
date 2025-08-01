package br.com.board.taskboard.view;

import br.com.board.taskboard.util.ConsolePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MainMenu {

    private final BoardMenu boardMenu;
    private final CardMenu cardMenu;
    private final CardActionsMenu cardActionsMenu;
    private final ListOptionsMenu listOptionsMenu;
    private final Scanner scanner;

    @Autowired
    public MainMenu(BoardMenu boardMenu, CardMenu cardMenu,
                    CardActionsMenu cardActionsMenu, ListOptionsMenu listOptionsMenu, Scanner scanner) {
        this.boardMenu = boardMenu;
        this.cardMenu = cardMenu;
        this.cardActionsMenu = cardActionsMenu;
        this.listOptionsMenu = listOptionsMenu;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("=== Menu do Taskboard ===");
            System.out.println("1. Criar Quadro");
            System.out.println("2. Adicionar Cartão");
            System.out.println("3. Deletar Cartão");
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
                     case 1 -> boardMenu.createBoard();
                    case 2 -> cardMenu.addCard();
                    case 3 -> cardMenu.deleteCard();
                    case 4 -> cardActionsMenu.moveCard();
                    case 5 -> cardActionsMenu.blockCard();
                    case 6 -> cardActionsMenu.unblockCard();
                    case 7 -> listOptionsMenu.listColumns();
                    case 8 -> listOptionsMenu.listCardMovements();
                    case 9 -> listOptionsMenu.listBlockHistory();
                    case 10 -> listOptionsMenu.listActiveBlocks();
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
}
