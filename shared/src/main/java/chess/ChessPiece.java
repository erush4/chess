package chess;

import java.util.Collection;
import java.util.Objects;

import static ui.EscapeSequences.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;
    private final ChessGame.TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        color = pieceColor;
        this.type = type;
    }

    @Override
    public String toString() {
        String output = "";
        switch (color){
            case BLACK -> output += SET_TEXT_COLOR_BLACK;
            case WHITE -> output += SET_TEXT_COLOR_WHITE;
        }
        switch (type){
            case KING -> output += BLACK_KING;
            case QUEEN -> output += BLACK_QUEEN;
            case ROOK -> output += BLACK_ROOK;
            case BISHOP -> output += BLACK_BISHOP;
            case KNIGHT -> output += BLACK_KNIGHT;
            case PAWN -> output += BLACK_PAWN;
        }
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        MoveCalculator calc = new MoveCalculator(board, myPosition);
        return calc.getValidMoves();
    }
}
