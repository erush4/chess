package chess;

public class PawnMoveCalculator extends ChessMoveCalculator {
    public PawnMoveCalculator(ChessBoard board, ChessPosition start) {
        super();
        calculateMove(board, start);
    }

    private void newMove(ChessPosition start, ChessPosition next, int lastRow) {
        if (next.getRow() != lastRow) {
            super.addMove(new ChessMove(start, next, null));
        } else {
            super.addMove(new ChessMove(start, next, ChessPiece.PieceType.KNIGHT));
            super.addMove(new ChessMove(start, next, ChessPiece.PieceType.QUEEN));
            super.addMove(new ChessMove(start, next, ChessPiece.PieceType.BISHOP));
            super.addMove(new ChessMove(start, next, ChessPiece.PieceType.ROOK));
        }
    }

    /*
   Pawns:
   can move forward UP TO two spaces on the first turn, then one space on all following turns
   can move one space forward diagonal (one each direction)
   promote to another piece on reaching the opposite side
    */
    @Override
    public void calculateMove(ChessBoard board, ChessPosition start) {
        int ahead = 0;
        int initRow = 0;
        int lastRow = 0;
        //sets direction and location of start
        switch (board.getPiece(start).getTeamColor()) {
            case WHITE:
                ahead = 1;
                initRow = 2;
                lastRow = 8;
                break;
            case BLACK:
                ahead = -1;
                initRow = 7;
                lastRow = 1;
                break;
        }
        //checks advance, no capture move
        ChessPosition next = new ChessPosition(start.getRow() + ahead, start.getColumn());
        if (board.getPiece(next) == null) {
            newMove(start, next, lastRow);
        }
        //checks double move at start
        if (start.getRow() == initRow) {
            next = new ChessPosition(start.getRow() + (2 * ahead), start.getColumn());
            if (board.getPiece(next) == null) {
                newMove(start, next, lastRow);
            }
        }
        //checks diagonal capture moves
        for (int i = -1; i <= 1; i +=2){
            next = new ChessPosition(start.getRow() + ahead, start.getColumn() + i);
            if (board.getPiece(next) != null && board.getPiece(next).getTeamColor() != board.getPiece(start).getTeamColor()) {
                newMove(start, next, lastRow);
            }
        }
    }
}
