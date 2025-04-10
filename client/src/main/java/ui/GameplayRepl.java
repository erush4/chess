package ui;

import model.ResponseException;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;

public class GameplayRepl extends ReplTemplate {
    public GameplayRepl() {
        super("leave");
    }

    private String redraw() {
        return "";
    }

    private String makeMove(String[] params) {
        return  "";
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
            case "leave" -> RESET_COLOR + "You have left the game.";
            case "resign" -> resign();
            case "highlight" -> highlightMoves(params);
            case "redraw" -> redraw();
            case "move" -> makeMove(params);
            case "help" -> help();
            default -> SET_TEXT_COLOR_RED + "Invalid command: please use one of the following:\n" + help();
        };
    }
}
