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
        ChessMoveCalculator calculator = null;
        switch (type) {
            case KING:
                break;
            case QUEEN:
                break;
            case BISHOP:
                calculator = new BishopMoveCalculator(board, myPosition);
                break;
            case KNIGHT:
                calculator  = new KnightMoveCalculator(board, myPosition);
                break;
            case ROOK:
                calculator = new RookMoveCalculator(board, myPosition);
                break;
            case PAWN:
                calculator = new PawnMoveCalculator(board, myPosition);
                break;
        }

        /*
        Queen:
        can move the same as rooks and bishops combined
        run both checks and see if the move works?
         */

        /*
        King:
        can move the same as queen, but only one square
        cannot move into a threatened spot, but check has yet to be implemented
         */
        assert calculator != null;
        return calculator.getMoves();
    }
}
