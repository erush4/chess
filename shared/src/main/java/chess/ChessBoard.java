package chess;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }
    public String toString(ChessGame.TeamColor color) {
        StringBuilder string = new StringBuilder();
        string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY);
        switch (color) {
            case BLACK -> {
                for (int i = 0; i < 7; i++) {
                    string.append(" ").append((char) ('h' - i)).append("\u2003");
                }
                string.append(" a " + EMPTY + RESET_COLOR + "\n");
                String nextSquareColor = SET_BG_COLOR_DARK_GREEN;
                for (int row = 1; row <= 8; row++) {
                    string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
                    string.append(" ").append(row).append(" ");
                    nextSquareColor = switchColor(nextSquareColor);
                    for (int col = 8; col >= 1; col--) {
                        nextSquareColor = rowToString(string, nextSquareColor, row, col);
                    }
                    string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
                    string.append(" ").append(row).append("\u2003").append(RESET_COLOR).append("\n");
                }
                string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY);
                for (int i = 0; i < 7; i++) {
                    string.append(" ").append((char) ('h' - i)).append("\u2003");
                }
                string.append(" a " + EMPTY + RESET_COLOR + "\n");
            }
            case WHITE -> {
                for (int col = 0; col < 7; col++) {
                    string.append(" ").append((char) ('a' + col)).append("\u2003");
                }
                string.append(" h " + EMPTY + RESET_COLOR + "\n");

                String nextSquareColor = SET_BG_COLOR_DARK_GREEN;
                for (int row = 8; row >= 1; row--) {
                    string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
                    string.append(" ").append(row).append(" ");
                    nextSquareColor = switchColor(nextSquareColor);
                    for (int col = 1; col <= 8; col++) {
                        nextSquareColor = rowToString(string, nextSquareColor, row, col);
                    }
                    string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
                    string.append(" ").append(row).append("\u2003").append(RESET_COLOR).append("\n");
                }
                string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY);
                for (int col = 0; col < 7; col++) {
                    string.append(" ").append((char) ('a' + col)).append("\u2003");
                }
                string.append(" h " + EMPTY + RESET_COLOR + "\n");
            }
        }

        return string.toString();
    }

    private static String switchColor(String nextSquareColor) {
        nextSquareColor = switch (nextSquareColor) {
            case SET_BG_COLOR_DARK_GREEN -> SET_BG_COLOR_LIGHT_GREY;
            case SET_BG_COLOR_LIGHT_GREY -> SET_BG_COLOR_DARK_GREEN;
            default -> SET_BG_COLOR_RED;
        };
        return nextSquareColor;
    }

    private String rowToString(StringBuilder string, String nextSquareColor, int row, int col) {
        string.append(nextSquareColor);
        var location = new ChessPosition(row, col);
        var piece = this.getPiece(location);
        if (piece == null) {
            string.append(EMPTY);
        } else {
            string.append(piece);
        }
        nextSquareColor = switchColor(nextSquareColor);
        return nextSquareColor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //pawns
        for (int i = 1; i <= 8; i++) {
            ChessPosition here = new ChessPosition(2, i);
            ChessPiece pawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            addPiece(here, pawn);
            here = new ChessPosition(7, i);
            pawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(here, pawn);
        }

        //champions
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));

        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));

        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));

        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));

    }
}
