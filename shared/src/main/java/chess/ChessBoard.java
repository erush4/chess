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

    public ChessPiece getPiece(int row, int col){
        return board[row - 1][col - 1];
    }

    @Override
    public String toString() {
        StringBuilder printStringBuild = new StringBuilder();
        printStringBuild.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY);
        for (int col = 0; col < 7; col++) {
            printStringBuild.append(" ").append((char) ('a' + col)).append("\u2003");
        }
        printStringBuild.append(" h " + EMPTY + RESET_COLOR + "\n");

        boolean isDark = true;
        for (int row = 8; row >= 1; row--) {
            printStringBuild.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
            printStringBuild.append(" ").append(row).append(" ");
            isDark = !isDark;
            for (int col = 1; col <= 8; col++) {
                squareToString(printStringBuild, isDark, row, col);
                isDark = !isDark;
            }
            printStringBuild.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
            printStringBuild.append(" ").append(row).append("\u2003").append(RESET_COLOR).append("\n");
        }
        printStringBuild.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY);
        for (int col = 0; col < 7; col++) {
            printStringBuild.append(" ").append((char) ('a' + col)).append("\u2003");
        }
        printStringBuild.append(" h " + EMPTY + RESET_COLOR + "\n");
        return printStringBuild.toString();
    }

    private void squareToString(StringBuilder string, boolean isDark, int row, int col) {
        var location = new ChessPosition(row, col);
        String squareColor;
        if (isDark) {
            squareColor = SET_BG_COLOR_DARK_GREEN;
        } else {
            squareColor = SET_BG_COLOR_LIGHT_GREY;
        }
        string.append(squareColor);

        var piece = getPiece(location);
        if (piece == null) {
            string.append(EMPTY);
        } else {
            string.append(piece);
        }
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
