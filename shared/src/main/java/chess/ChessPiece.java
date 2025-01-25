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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
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
        switch (type) {
            case KING:
            case QUEEN:
            case BISHOP:
            case KNIGHT:
            case ROOK:
            case PAWN:
        }
        /*
        Pawns:
        can move forward UP TO two spaces on the first turn, then one space on all following turns
        can move one space forward diagonal (one each direction)
        promote to another piece on reaching the opposite side
         */

        /*
        Rooks:
        can move any number of spaces in one direction until they hit a piece
        if the piece is the opposite color, can take (move onto that square)
        if the piece is the same color, can move adjacent
        blockable, so should be calculated recursively
         */

        /*
        Knights:
        can move in two directions: one space in one direction, two spaces in another
        unblockable, unless there is a piece of the same color at the final square
         */

        /*
        bishops: can move in two directions: an equivalent number of spaces in each
        if the piece is the opposite color, can take
        if the piece is the same color, cannot take
        blockable, so should be calculated recursively
         */

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

        throw new RuntimeException("Not implemented");
    }
}
