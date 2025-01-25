package chess;

import java.util.Collection;

public class QueenMoveCalculator extends ChessMoveCalculator{
    QueenMoveCalculator(ChessBoard board, ChessPosition start) {
        super();
        calculateMove(board, start);
    }
    @Override
    public void calculateMove(ChessBoard board, ChessPosition start) {

        BishopMoveCalculator diagonal = new BishopMoveCalculator(board, start);
        RookMoveCalculator lines = new RookMoveCalculator(board, start);

        Collection<ChessMove> diagonalMoves = diagonal.getMoves();
        Collection<ChessMove> lineMoves = lines.getMoves();
        for (ChessMove move : diagonalMoves) {
            super.addMove(move);
        }
        for (ChessMove move : lineMoves) {
            super.addMove(move);
        }
    }
}
