package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;
    private final ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.teamColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, teamColor);
    }

    //The various different chess piece options
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    //@return Which team this chess piece belongs to
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    //@return which type of chess piece this piece is
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
        ChessMoveCalculator calculator = switch (type) {
            case KING -> new KingMoveCalculator(board, myPosition);
            case QUEEN -> new QueenMoveCalculator(board, myPosition);
            case BISHOP -> new BishopMoveCalculator(board, myPosition);
            case KNIGHT -> new KnightMoveCalculator(board, myPosition);
            case ROOK -> new RookMoveCalculator(board, myPosition);
            case PAWN -> new PawnMoveCalculator(board, myPosition);
        };
        return calculator.getMoves();
    }
}
