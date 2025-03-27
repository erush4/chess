package ui;

import model.ResponseException;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

abstract class ReplTemplate {
    protected String exitString;
    public ReplTemplate(String exitString){
        this.exitString = exitString;
    }
    private String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String command;
            if (tokens.length > 0) {
                command = tokens[0];
            } else {
                command = "help";
            }
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return functions(command, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void start() {
        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equals(exitString)) {
            printPrompt();
            input = scanner.nextLine();

            try {
                var result = eval(input);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(SET_TEXT_COLOR_RED + msg);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_COLOR + ">>>" + SET_TEXT_COLOR_GREEN);
    }

    abstract String help();

    abstract String functions(String command, String[] params) throws ResponseException;
}

