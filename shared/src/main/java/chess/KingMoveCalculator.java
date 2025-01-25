package chess;

public class KingMoveCalculator extends ChessMoveCalculator {
    public KingMoveCalculator(ChessBoard board, ChessPosition start) {
        super();
        calculateMove(board, start);
    }

    @Override
    public void calculateMove(ChessBoard board, ChessPosition start) {
        for(int i = -1; i <= 1; i +=2) {
            for(int j = -1; j <= 1; j +=2) {
                ChessPosition next = new ChessPosition(start.getRow() + i, start.getColumn() + j);
                ChessPiece nextSpace = board.getPiece(next);
                if (nextSpace == null || nextSpace.getTeamColor() != board.getPiece(next).getTeamColor()) {
                    ChessMove newMove = new ChessMove(start, next, null);
                    addMove(newMove);
                }
            }
        }
    }
}
