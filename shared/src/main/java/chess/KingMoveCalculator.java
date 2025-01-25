package chess;

public class KingMoveCalculator extends ChessMoveCalculator {
    public KingMoveCalculator(ChessBoard board, ChessPosition start) {
        super();
        calculateMove(board, start);
    }

    @Override
    public void calculateMove(ChessBoard board, ChessPosition start) {
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j ++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int row = start.getRow() + i;
                int column = start.getColumn() + j;
                if (row <= 0 || row > 8 || column <= 0 || column > 8) {
                    continue;
                }
                ChessPosition next = new ChessPosition(row, column);
                ChessPiece nextSpace = board.getPiece(next);
                boolean validSquare = nextSpace == null || nextSpace.getTeamColor() != board.getPiece(start).getTeamColor();
                if (validSquare) {
                    ChessMove newMove = new ChessMove(start, next, null);
                    addMove(newMove);
                }
            }
        }
    }
}
