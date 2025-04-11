package ui;

import chess.ChessGame;
import chess.ChessPosition;
import model.GameData;
import model.ResponseException;
import server.NotificationHandler;
import server.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import static ui.EscapeSequences.*;

public class GameplayLoop extends Repl implements NotificationHandler {
    WebSocketFacade webSocketFacade;
    int gameID;
    String authToken;
    GameData game;
    ChessGame.TeamColor team;

    public GameplayLoop(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        super("leave");
        try {
            webSocketFacade = new WebSocketFacade(this);
        } catch (ResponseException e) {
            System.out.print(SET_TEXT_COLOR_RED + "Could not connect to server" + e.getMessage());
            return;
        }
        this.authToken = authToken;
        this.gameID = gameID;
        this.team = teamColor;
        start();
    }

    private String redraw(String[] params) {
        if (params.length != 0) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        return game.game().toString();
    }

    private String makeMove(String[] params) {
        if (params.length != 4) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        return null;
    }

    private String resign(String[] params) {
        if (params.length != 0) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        return null;
    }

    private String highlightMoves(String[] params) {
        if (params.length != 2) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        int row = 1 +  params[1].charAt(0) - 'a';
        int col = Integer.parseInt(params[0]);
        return game.game().projectValidMoves(new ChessPosition(row, col), team);
    }

    @Override
    String help() {
        return SET_TEXT_COLOR_BLUE + "move <START ROW> <START COLUMN> <END ROW> <END COLUMN>"
                + SET_TEXT_COLOR_LIGHT_GREY + " - makes the selected chess move, if valid\n"
                + SET_TEXT_COLOR_BLUE + "highlight <ROW> <COLUMN>" + SET_TEXT_COLOR_LIGHT_GREY
                + " - highlights valid moves for the selected piece\n"
                + SET_TEXT_COLOR_BLUE + "redraw" + SET_TEXT_COLOR_LIGHT_GREY + " - redraws the board\n"
                + SET_TEXT_COLOR_BLUE + "resign" + SET_TEXT_COLOR_LIGHT_GREY + " - resign from the game\n"
                + SET_TEXT_COLOR_BLUE + "leave" + SET_TEXT_COLOR_LIGHT_GREY + " - leave the game\n"
                + SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_LIGHT_GREY + " - show this page again";
    }

    @Override
    String functions(String command, String[] params) throws ResponseException {
        return switch (command) {
            case "leave" -> leave(params);
            case "resign" -> resign(params);
            case "highlight" -> highlightMoves(params);
            case "redraw" -> redraw(params);
            case "move" -> makeMove(params);
            case "help" -> help();
            default -> SET_TEXT_COLOR_RED + "Invalid command: please use one of the following:\n" + help();
        };
    }

    private String leave(String[] params) {
        if (params.length != 0) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        try {
            webSocketFacade.leave(gameID, authToken);
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Could not connect to server";
        }
        return "";
    }

    @Override
    public void notification(NotificationMessage message) {

    }

    @Override
    public void error(ErrorMessage message) {

    }

    @Override
    public void load_game(LoadGameMessage message) {
        game = message.getGame();
    }
}