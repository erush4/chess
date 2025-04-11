package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
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
        return game.game().projectValidMoves(null, team);
    }

    private String makeMove(String[] params) { //TODO
        if (params.length > 3 || params.length < 2) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        ChessPosition startLocation;
        ChessPosition endLocation;
        try {
            startLocation = parseLocation(params[0]);
            endLocation = parseLocation(params[1]);
        } catch (Exception e) {
            return SET_TEXT_COLOR_RED + "Something is wrong with your input. Please try again.";
        }
        var piece = game.game().getBoard().getPiece(startLocation);
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            if (params.length == 3) {
                return SET_TEXT_COLOR_RED + "Invalid Promotion. Cannot promote non-pawn pieces.";
            }
        } else if (endLocation.getRow() == 1 || endLocation.getRow() == 8) {
            if (params.length == 2) {
                return SET_TEXT_COLOR_RED + "Must include promotion piece when moving to end of board.";
            }
        } else {
            if (params.length == 3) {
                return SET_TEXT_COLOR_RED + "Cannot promote before reaching the end of the board.";
            }
        }
        ChessPiece.PieceType type = null;
        if (params.length == 3) {
            switch (params[2]) {
                case "queen" -> type = ChessPiece.PieceType.QUEEN;
                case "knight" -> type = ChessPiece.PieceType.KNIGHT;
                case "bishop" -> type = ChessPiece.PieceType.BISHOP;
                case "rook" -> type = ChessPiece.PieceType.ROOK;
                case null, default -> {
                    return SET_TEXT_COLOR_RED + "Invalid promotion piece type.";
                }
            }
        }
        ChessMove move = new ChessMove(startLocation, endLocation, type);
        try {
            webSocketFacade.makeMove(gameID, authToken, move);
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Could not connect to server";
        } catch (IllegalStateException e) {
            return SET_TEXT_COLOR_RED + "The connection timed out.";
        }
        return "";
    }

    private String resign(String[] params) {
        if (params.length != 0) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        try {
            webSocketFacade.resign(gameID, authToken);
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Could not connect to server";
        } catch (IllegalStateException e) {
            return SET_TEXT_COLOR_RED + "The connection timed out.";
        }
        return "";
    }

    private String highlightMoves(String[] params) {
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "Incorrect number of parameters. Please try again.";
        }
        if (params[0].length() > 2) {
            return SET_TEXT_COLOR_RED + "Please enter a valid square.";
        }
        try {
            return game.game().projectValidMoves(parseLocation(params[0]), team);
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
    String functions(String command, String[] params) {
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
        } catch (IllegalStateException e) {
            return SET_TEXT_COLOR_RED + "The connection timed out.";
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
    public void loadGame(LoadGameMessage message) {
        this.game = message.getGame();
        System.out.println("\n" + game.game().projectValidMoves(null, team));
        System.out.print(RESET_COLOR + ">>>" + SET_TEXT_COLOR_GREEN);
    }

    private ChessPosition parseLocation(String string) {
        int row = (string.charAt(1)) - '0';
        int col = 1 + string.charAt(0) - 'a';
        return new ChessPosition(row, col);
    }
}