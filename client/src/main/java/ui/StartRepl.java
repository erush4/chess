package ui;

import model.*;
import server.ServerFacade;

import static ui.EscapeSequences.*;


public class StartRepl extends ReplTemplate{
    private static ServerFacade server = new ServerFacade("8080");
    public StartRepl(String port) {
        super("quit");
        server = new ServerFacade(port);
    }

    private String login(String[] params) {
        if (params.length != 2) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        LoginRequest requestObject = new LoginRequest(params[0], params[1]);
        LoginResponse response;
        try {
            response = server.login(requestObject);
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + switch (e.getStatusCode()) {
                case 401 -> "The username/password combination you entered was incorrect. Please try again";
                case 500 -> "There was an error on our end. Please try again later.";
                default -> throw new RuntimeException("bad error code");
            };
        }
        String authtoken = response.authToken();
        String username = response.username();
        System.out.println(RESET_COLOR + "Welcome, " + username);

        new LoggedInRepl(authtoken, username, server);

        return "Goodbye, " + username;
    }

    private String register(String[] params) {
        if (params.length != 3) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        RegisterRequest requestObject = new RegisterRequest(params[0], params[1], params[2]);
        RegisterResponse response;
        try {
            response = server.register(requestObject);
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + switch (e.getStatusCode()) {
                case 403 -> "This username is already taken. Please try another.";
                case 400 -> "There's something wrong with your request. Please make sure all fields are correctly formatted.";
                case 500 -> "There was an error on our end. Please try again later.";
                default -> throw new RuntimeException("bad error code");
            };

        }

        String authtoken = response.authToken();
        String username = response.username();

        System.out.println("Welcome, " + username + "! Your account has been created successfully.");
        new LoggedInRepl(authtoken, username, server);
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
    String Functions(String command, String[] params) {
            return switch (command) {
            case "quit" -> RESET_COLOR + "Have a nice day! ☺";
            case "login" -> login(params);
            case "register" -> register(params);
            case "help" -> help();
            default -> SET_TEXT_COLOR_RED + "Invalid command: please use one of the following:\n" + help();
        };
    }

}
