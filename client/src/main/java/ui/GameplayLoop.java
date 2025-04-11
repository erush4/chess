package ui;

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

    public GameplayLoop(String authToken, int gameID) {
        super("leave");
        try {
            webSocketFacade = new WebSocketFacade(this);
        } catch (ResponseException e) {
            System.out.print(SET_TEXT_COLOR_RED + "Could not connect to server");
        }
        this.authToken = authToken;
        this.gameID = gameID;
        start();
    }

    private String redraw() {
        return "";
    }

    private String makeMove(String[] params) {
        return "";
    }

    private String resign() {
        return "";
    }

    private String highlightMoves(String[] params) {
        return "";
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
            case "leave" -> leave();
            case "resign" -> resign();
            case "highlight" -> highlightMoves(params);
            case "redraw" -> redraw();
            case "move" -> makeMove(params);
            case "help" -> help();
            default -> SET_TEXT_COLOR_RED + "Invalid command: please use one of the following:\n" + help();
        };
    }

    private String leave() {
        try {
            webSocketFacade.leave(gameID, authToken);
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Could not connect to server";
        }
        return RESET_COLOR + "You have left the game.";
    }

    @Override
    public void notification(NotificationMessage message) {

    }

    @Override
    public void error(ErrorMessage message) {

    }

    @Override
    public void load_game(LoadGameMessage message) {

    }
}