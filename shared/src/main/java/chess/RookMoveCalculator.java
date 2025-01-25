package chess;

public class RookMoveCalculator extends ChessMoveCalculator{
    private ChessBoard board;
    private ChessPosition start;
    public RookMoveCalculator(ChessBoard board, ChessPosition start) {
        super();
        calculateMove(board, start);
    }

    /*
    Rooks:
    can move any number of spaces in one direction until they hit a piece
    if the piece is the opposite color, can take (move onto that square)
    if the piece is the same color, can move adjacent
    blockable, so should be calculated recursively
    */
    @Override
    public void calculateMove(ChessBoard board, ChessPosition start) {
        this.board = board;
        this.start = start;
        for (int direction = -1; direction <= 1; direction += 2) {
            rowMove(direction, start);
            columnMove(direction, start);
        }

    }

    private void rowMove(int direction, ChessPosition here) { //recursively checks vertically in the direction given
        int nextRow = here.getRow() + direction;
        if (nextRow < 1 || nextRow > 8) {
            return;
        }
        ChessPosition next = new ChessPosition(nextRow, here.getColumn());
        if (board.getPiece(next) == null) {
            super.addMove(new ChessMove(this.start, next, null));
            rowMove(direction, next);
        } else if (board.getPiece(next).getTeamColor() != board.getPiece(start).getTeamColor()) {
            super.addMove(new ChessMove(this.start, next, null));
        }
    }

    private void columnMove(int direction, ChessPosition here) { //recursively checks horizontally in the direction given
        int nextColumn = here.getColumn() + direction;
        if (nextColumn < 1 || nextColumn > 8) {
            return;
        }
        ChessPosition next = new ChessPosition(here.getRow(), nextColumn);
        if (board.getPiece(next) == null) {
            super.addMove(new ChessMove(this.start, next, null));
            columnMove(direction, next);
        } else if (board.getPiece(next).getTeamColor() != board.getPiece(start).getTeamColor()) {
            super.addMove(new ChessMove(this.start, next, null));
        }
    }
}

