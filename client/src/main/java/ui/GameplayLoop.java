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
            this.authToken = authToken;
            this.gameID = gameID;
            this.team = teamColor;
            webSocketFacade.connect(gameID, authToken);
        } catch (ResponseException e) {
            System.out.print(SET_TEXT_COLOR_RED + "Could not connect to server" + e.getMessage());
            return;
        }

        start();
    }

    private String redraw(String[] params) {
        if (params.length != 0) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        return game.game().getBoard().toString(team);
    }

    private String makeMove(String[] params) { //TODO
        if (params.length != 4) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        return null;
    }

    private String resign(String[] params) {
        if (params.length != 0) { //TODO
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        return null;
    }

    private String highlightMoves(String[] params) {
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        if (params[0].length() > 2) {
            return SET_TEXT_COLOR_RED + "Please enter a valid square.";
        }
        int row = (params[0].charAt(0)) - '0';
        int col = 1 + params[0].charAt(1) - 'a';
        try {
            return game.game().projectValidMoves(new ChessPosition(row, col), team);
        } catch (NullPointerException e) {
            return SET_TEXT_COLOR_RED + "There is no piece on the selected square.";
        } catch (IndexOutOfBoundsException e) {
            return SET_TEXT_COLOR_RED + "Please enter a valid square.";
        }
    }

    @Override
    String help() {
        return SET_TEXT_COLOR_BLUE + "move <START POSITION> <END POSITION> <PROMOTION PIECE>"
                + SET_TEXT_COLOR_LIGHT_GREY + " - makes the selected chess move, if valid. \n"
                + SET_TEXT_COLOR_BLUE + "highlight <POSITION>" + SET_TEXT_COLOR_LIGHT_GREY
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
        System.out.println("\n" + RESET_TEXT_COLOR + message.getMessage());
        System.out.print(RESET_COLOR + ">>>" + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void error(ErrorMessage message) {
        System.out.println("\n" + SET_TEXT_COLOR_RED + message.getErrorMessage());
        System.out.print(RESET_COLOR + ">>>" + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void load_game(LoadGameMessage message) {
        this.game = message.getGame();
        var p = new String[0];
        System.out.println("\n" + game.game().getBoard().toString(team));
        System.out.print(RESET_COLOR + ">>>" + SET_TEXT_COLOR_GREEN);
    }
}