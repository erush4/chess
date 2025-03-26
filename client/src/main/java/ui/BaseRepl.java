package ui;

import model.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;


public class BaseRepl {
    private static String RESET;
    private static ServerFacade server;

    public BaseRepl() {
        RESET = RESET_BG_COLOR + RESET_TEXT_COLOR;
        server = new ServerFacade("0");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();

            try {
                result = eval(input);
                System.out.print(result);
            } catch (Throwable e) {
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

    public String help() {
        return """
                - quit
                - login <username> <password>
                - register <username> <password> <email>
                - help
                """;
    }

    public String login(String[] params) throws ResponseException {
        if (params.length > 2) {
            throw new RuntimeException("Too many parameters");
        }
        LoginRequest requestObject = new LoginRequest(params[0], params[1]);
        LoginResponse response = server.login(requestObject);
        String authtoken = response.authToken();
        String username = response.username();
        System.out.println("Welcome, " + username);

        new LoggedInRepl(authtoken, username);

        return "Goodbye, " + username;
    }

    public String register(String[] params) throws ResponseException {
        if (params.length > 3) {
            throw new RuntimeException("Too many parameters");
        }
        RegisterRequest requestObject = new RegisterRequest(params[0], params[1], params[2]);
        RegisterResponse response = server.register(requestObject);
        String authtoken = response.authToken();
        String username = response.username();

        System.out.println("Welcome, " + username + "! Your account has been created successfully.");
        new LoggedInRepl(authtoken, username);
        return "Goodbye, " + username;
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>>" + SET_TEXT_COLOR_GREEN);
    }

}
