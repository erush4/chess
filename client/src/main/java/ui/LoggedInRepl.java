package ui;

import model.ResponseException;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class LoggedInRepl extends ReplTemplate {
    String authtoken;
    String username;

    public LoggedInRepl(String authtoken, String username) {
        super("logout");
        this.authtoken = authtoken;
        this.username = username;
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


        return "";
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
