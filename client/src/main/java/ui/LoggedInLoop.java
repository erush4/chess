package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.*;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class LoggedInLoop extends Repl {
    String authtoken;
    String username;
    ServerFacade server;
    ArrayList<GameData> games;
    HashMap<Integer, Integer> gameIDs;

    public LoggedInLoop(String authtoken, String username, ServerFacade server) {
        super("logout");
        this.authtoken = authtoken;
        this.username = username;
        this.server = server;
        this.gameIDs = new HashMap<>();
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
    String functions(String command, String[] params) {
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
        gameIDs.put(0, response.gameID());
        return RESET_COLOR + "Your game " + SET_TEXT_COLOR_YELLOW + gameName + RESET_COLOR +
                " has been successfully created!\nYou can join it now with temporary ID #" +
                SET_TEXT_COLOR_YELLOW + "0" +RESET_COLOR + ".";
    }


    String join(String[] params) {
        try {
            if (params.length != 2) {
                return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
            }
            int clientGameID = Integer.parseInt(params[0]);
            ChessGame.TeamColor color;
            color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
            int gameID = gameIDs.get(clientGameID);
            var request = new JoinGameRequest(color, gameID);

            server.joinGame(request, authtoken);
            new GameplayLoop(authtoken, gameID,color);
            return RESET_COLOR + "You have left the game.";
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
        } catch (NullPointerException e) {
            return SET_TEXT_COLOR_RED + "Please use the command \"" + SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_RED +
                    "\" to get the list of joinable games before observing.";
        }

    }

    String observe(String[] params) {
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        try {
            int clientGameID = Integer.parseInt(params[0]);
            int gameID = gameIDs.get(clientGameID);
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            return RESET_COLOR + "Now observing game #" + SET_TEXT_COLOR_YELLOW + clientGameID + RESET_COLOR
                    + RESET_COLOR + ".\n" + board.toString(ChessGame.TeamColor.WHITE);
        } catch (NumberFormatException e) {
            return SET_TEXT_COLOR_RED + "Your game ID is not a number. Please try again.";
        } catch (NullPointerException e) {
            return SET_TEXT_COLOR_RED + "Please use the command \"" + SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_RED +
                    "\" to get the list of joinable games before observing.";
        }
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
        games = (ArrayList<GameData>) response.games();
        StringBuilder string = new StringBuilder(RESET_COLOR + "Game List:\n");
        gameIDs.clear();
        for (GameData game : games) {
            int clientGameID = games.indexOf(game) + 1;
            gameIDs.put(clientGameID, game.gameID());
            string.append("Game #" + SET_TEXT_COLOR_YELLOW).append(clientGameID).append(RESET_COLOR + "\n");
            string.append("\tName: " + SET_TEXT_COLOR_YELLOW).append(game.gameName()).append(RESET_COLOR +"\n");
            string.append("\tWhite Player: "+SET_TEXT_COLOR_YELLOW).append(nameCheck(game.whiteUsername())).append(RESET_COLOR+ "\n");
            string.append("\tBlack Player: " + SET_TEXT_COLOR_YELLOW).append(nameCheck(game.blackUsername())).append(RESET_COLOR + "\n\n");
        }
        return string.toString();
    }
    private String nameCheck (String name){
        return Objects.requireNonNullElse(name, "");
    }
}
