package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.*;
import server.ServerFacade;

import static ui.EscapeSequences.*;

public class LoggedInRepl extends ReplTemplate {
    String authtoken;
    String username;
    ServerFacade server;

    public LoggedInRepl(String authtoken, String username, ServerFacade server) {
        super("logout");
        this.authtoken = authtoken;
        this.username = username;
        this.server = server;
        start();
    }

    @Override
    String help() {
        return SET_TEXT_COLOR_BLUE + "create <NAME>" + SET_TEXT_COLOR_LIGHT_GREY + " - create a new game\n"
                + SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_LIGHT_GREY + " - lists all games\n"
                + SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]" + SET_TEXT_COLOR_LIGHT_GREY + " - join an existing game\n"
                + SET_TEXT_COLOR_BLUE + "observe <ID>" + SET_TEXT_COLOR_LIGHT_GREY + " - observe a game\n"
                + SET_TEXT_COLOR_BLUE + "logout" + SET_TEXT_COLOR_LIGHT_GREY + " - log out of the application\n"
                + SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_LIGHT_GREY + " - show this page again";
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
        return RESET_COLOR + "Your game " + SET_TEXT_COLOR_YELLOW + gameName + RESET_COLOR +
                " has been successfully created with ID " + SET_TEXT_COLOR_YELLOW + response.gameID() + RESET_COLOR + "!";
    }


    String join(String[] params) {
        try {
            if (params.length != 2) {
                return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
            }
            int gameID = Integer.parseInt(params[0]);
            ChessGame.TeamColor color;
            color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
            var request = new JoinGameRequest(color, gameID);

            server.joinGame(request, authtoken);
            ChessBoard board =  new ChessBoard();
            board.resetBoard();
            return RESET_COLOR + "Successfully joined game #" + SET_TEXT_COLOR_YELLOW + gameID + RESET_COLOR + " as "
                    + SET_TEXT_COLOR_YELLOW + color + RESET_COLOR + ".\n" + board.toString(color);
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + switch (e.getStatusCode()) {
                case 400 ->
                        "Something went wrong with your request. Please ensure all fields are correct and try again.";
                case 401 -> "Your session is invalid. You may need to restart the application.";
                case 403 ->
                        "This color is already taken for this game. Please try a different color, or join as an observer.";
                case 500 -> "There was an error on our end. Please try again later.";
                default -> throw new RuntimeException("bad error code");
            };

        } catch (NumberFormatException e) {
            return SET_TEXT_COLOR_RED + "Your game ID is not a number. Please try again.";
        } catch (IllegalArgumentException e) {
            return SET_TEXT_COLOR_RED + "You have improperly specified the color. Please ensure you spelled it correctly.";
        }
    }

    String observe(String[] params) {
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        return SET_TEXT_COLOR_RED + "This functionality will be implemented in phase 6.";
    }

    String list(String[] params) {
        if (params.length != 0) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        ListGamesResponse response;
        try {
            response = server.listGames(authtoken);
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + switch (e.getStatusCode()) {
                case 401 -> "Your session is invalid. You may need to restart the application.";
                case 500 -> "There was an error on our end. Please try again later.";
                default -> throw new RuntimeException("bad error code");
            };
        }
        return RESET_COLOR + response.toString();
    }
}
