package br.com.board.taskboard.util;

public class ConsolePrinter {

    public static void printSuccess(String message) {
        System.out.println(AnsiColors.GREEN + message + AnsiColors.RESET);
    }

    public static void printError(String message) {
        System.out.println(AnsiColors.RED + message + AnsiColors.RESET);
    }

    public static void printWarning(String message) {
        System.out.println(AnsiColors.YELLOW + message + AnsiColors.RESET);
    }

    public static void printInfo(String message) {
        System.out.println(AnsiColors.CYAN + message + AnsiColors.RESET);
    }
}