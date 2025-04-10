package ui;

import model.ResponseException;

import static ui.EscapeSequences.RESET_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class GameplayRepl extends ReplTemplate {
    public GameplayRepl() {
        super("leave");
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
        return "";
    }

    @Override
    String functions(String command, String[] params) throws ResponseException {
        return switch (command) {
            case "leave" -> RESET_COLOR + "Leaving the game...";
            case "resign" -> resign();
            case "highlight" -> highlightMoves(params);
            case "redraw" -> redraw();
            case "move" -> makeMove(params);
            case "help" -> help();
            default -> SET_TEXT_COLOR_RED + "Invalid command: please use one of the following:\n" + help();
        };
    }
}
