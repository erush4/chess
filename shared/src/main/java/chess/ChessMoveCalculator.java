package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ChessMoveCalculator {
    private Collection<ChessMove> moves;
    public ChessMoveCalculator() {
        moves = new ArrayList<ChessMove>();
    }
    public void addMove(ChessMove move) {
        moves.add(move);
    }
    public Collection<ChessMove> getMoves() {
        return moves;
    }
}
