package ui;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;


public class BaseRepl {
    private static String RESET;
    private static Client client;
    BaseRepl(){
        RESET = RESET_BG_COLOR + RESET_TEXT_COLOR;
    }
    public void start(){
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();

            try {
                result = eval(input);
                System.out.print(result);
            } catch (Throwable e){
                var msg = e.getMessage();
                System.out.print(SET_TEXT_COLOR_RED + msg);
            }
        }
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
            return switch (command) {
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String help(){
        return """
                - quit
                - login <username> <password>
                - register <username> <email> <password>
                - help
                """;
    }

    public String login(String[] params){
        return null;
    }

    public String register(String[] params){
        return null;
    }

    private void printPrompt(){
        System.out.print("\n" + RESET + ">>>" + SET_TEXT_COLOR_GREEN);
    }

}
