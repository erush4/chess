package chess;


public class KnightMoveCalculator extends ChessMoveCalculator {
    public KnightMoveCalculator(ChessBoard board, ChessPosition start) {
        super();
        calculateMove(board, start);
    }

    /*
        Knights:
        can move in two directions: one space in one direction, two spaces in another
        unblockable, unless there is a piece of the same color at the final square
         */
    @Override
    public void calculateMove(ChessBoard board, ChessPosition start) {
        for (int i = 1; i <= 2; i++){
            int j = 3 - i; // forces an L shape (over 1, across 2, regardless of which way)
            for (int i2 = -i; i2 <= i; i2 += (2*i)){ //checks both directions on the row
                for (int j2 = -j; j2 <= j; j2 += (2*j)){ //checks both directions on the column
                    int row = start.getRow() + i2;
                    int column = start.getColumn() + j2;
                    if (row <= 0 || row > 8 || column <= 0 || column > 8) {
                        continue;
                    }

                    ChessPosition newPosition = new ChessPosition(row, column);
                    ChessPiece nextSpace = board.getPiece(newPosition);

                    if (nextSpace == null || nextSpace.getTeamColor()!= board.getPiece(start).getTeamColor()) {
                        ChessMove newMove = new ChessMove(start, newPosition, null);
                        super.addMove(newMove);
                    }
                }
            }

        }
    }
}
