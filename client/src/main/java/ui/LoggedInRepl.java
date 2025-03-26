package ui;

import model.CreateGameRequest;
import model.CreateGameResponse;
import model.ResponseException;
import server.ServerFacade;

import java.util.HashMap;

import static ui.EscapeSequences.*;

public class LoggedInRepl extends ReplTemplate {
    String authtoken;
    String username;
    ServerFacade server;
    HashMap<Integer, String> games;

    public LoggedInRepl(String authtoken, String username, ServerFacade server) {
        super("logout");
        this.authtoken = authtoken;
        this.username = username;
        this.server = server;
    }

    @Override
    String help() {
        return SET_TEXT_COLOR_BLUE + "create <NAME>" + SET_TEXT_COLOR_LIGHT_GREY + " - create a new game\n" +
                SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_LIGHT_GREY + " - lists all games\n" +
                SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]" + SET_TEXT_COLOR_LIGHT_GREY + " - join an existing game\n" +
                SET_TEXT_COLOR_BLUE + "observe <ID>" + SET_TEXT_COLOR_LIGHT_GREY + " - observe a game\n" +
                SET_TEXT_COLOR_BLUE + "logout" + SET_TEXT_COLOR_LIGHT_GREY + " - log out of the application\n" +
                SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_LIGHT_GREY + " - show this page again";
    }

    @Override
    String Functions(String command, String[] params) {
        return switch (command) {
            case "create" -> create(params);
            case "list" -> list(params);
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "logout" -> RESET_COLOR;
            case "help" -> help();
            default -> SET_TEXT_COLOR_RED + "Invalid command: please use one of the following:\n" + help();
        };
    }

    String create(String[] params) {
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        String gameName = params[0];
        var request = new CreateGameRequest(gameName);
        CreateGameResponse response;
        try {
            response = server.createGame(request, authtoken);
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + switch (e.getStatusCode()) {
                case 401 -> "Your session is invalid. You may need to restart the application.";
                case 500 -> "There was an error on our end. Please try again later.";
                default -> throw new RuntimeException("bad error code");
            };
        }
        games.put(response.gameID(), gameName);
        return RESET_COLOR + "Your game " + SET_TEXT_BOLD + gameName + RESET_TEXT_BOLD_FAINT + "has been successfully created!";
    }


    String join(String[] params) {
        if (params.length != 2) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        return "";
    }

    String observe(String[] params) {
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        return "";
    }

    String list(String[] params) {
        if (params.length != 0) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        return "";
    }
}
