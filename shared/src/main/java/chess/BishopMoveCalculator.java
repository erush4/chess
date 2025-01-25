package chess;

public class BishopMoveCalculator extends ChessMoveCalculator {
    private ChessBoard board;
    private ChessPosition start;

    public BishopMoveCalculator(ChessBoard board, ChessPosition start) {
        super();
        calculateMove(board, start);
    }

    /*
    bishops: can move in two directions: an equivalent number of spaces in each
    if the piece is the opposite color, can take
    if the piece is the same color, cannot take
    blockable, so should be calculated recursively
     */
    @Override
    public void calculateMove(ChessBoard board, ChessPosition start) {
        this.board = board;
        this.start = start;
        for (int x = -1; x <= 1; x += 2) {
            for (int y = -1; y <= 1; y += 2) {
                calculateMove(x, y, start);
            }
        }
    }

    private void calculateMove(int xDirection, int yDirection, ChessPosition here) {
        int nextRow = here.getRow() + yDirection;
        int nextCol = here.getColumn() + xDirection;
        if (nextRow < 1 || nextRow > 8 || nextCol < 1 || nextCol > 8) {
            return;
        }

        ChessPosition next = new ChessPosition(nextRow, nextCol);
        ChessPiece nextSpace = this.board.getPiece(next);
        if (nextSpace == null) {
            super.addMove(new ChessMove(this.start, next, null));
            calculateMove(xDirection, yDirection, next);

        } else if (nextSpace.getTeamColor() != this.board.getPiece(this.start).getTeamColor()) {
            super.addMove(new ChessMove(this.start, next, null));
        }
    }
}