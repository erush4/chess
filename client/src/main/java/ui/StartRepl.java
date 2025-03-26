package ui;

import model.*;
import server.ServerFacade;

import static ui.EscapeSequences.*;


public class StartRepl extends ReplTemplate{
    private static ServerFacade server;
    public StartRepl(String port) {
        super("quit");
        server = new ServerFacade(port);

    }

    private String login(String[] params) throws ResponseException {
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

    private String register(String[] params) throws ResponseException {
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

    @Override
    String help() {
        return SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_LIGHT_GREY + " - exit the application\n" +
                SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_LIGHT_GREY + " - login to begin playing\n" +
                SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_LIGHT_GREY + " - create an account\n" +
                SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_LIGHT_GREY + " - show this page again";
    }

    @Override
    String Functions(String command, String[] params) throws ResponseException {
        return switch (command) {
            case "quit" -> "";
            case "login" -> login(params);
            case "register" -> register(params);
            case "help" -> help();
            default -> SET_TEXT_COLOR_RED + "Invalid command: please use one of the following:\n" + help();
        };
    }

}
