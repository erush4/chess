package chess;

import java.util.Collection;
import java.util.HashSet;

public class MoveCalculator {
    private final ChessGame.TeamColor color;
    private final HashSet<ChessMove> validMoves;
    private final ChessPosition start;
    private final ChessBoard board;


    public MoveCalculator(ChessBoard board, ChessPosition start) {
        validMoves = new HashSet<>();
        this.board = board;
        ChessPiece piece = board.getPiece(start);
        ChessPiece.PieceType type = piece.getPieceType();
        this.start = start;
        color = piece.getTeamColor();

        switch(type){
            case KING:
                championMoveCalculator(1,1,false);
                championMoveCalculator(0,1,false);
                championMoveCalculator(1,0,false);
                break;
            case QUEEN:
                championMoveCalculator(0,1,true);
                championMoveCalculator(1,0,true);
            case BISHOP:
                championMoveCalculator(1,1,true);
                break;
            case ROOK:
                championMoveCalculator(0,1,true);
                championMoveCalculator(1,0,true);
                break;
            case KNIGHT:
                championMoveCalculator(2,1,false);
                championMoveCalculator(1,2,false);
                break;
            case PAWN:
                pawnMoveCalculator();
        }
    }

    private boolean outOfBounds(int line){
        return (line < 1 || line > 8);
    }

    private void championMoveCalculator(int rowMove, int colMove, boolean recursive) {

        int i = 0;
        int row = -rowMove;
        while (i < 2) {
            i++;
            int col = -colMove;
            int j = 0;
            int newRow = start.getRow() + row;
            if (outOfBounds(newRow)) {
                row += 2 * rowMove;
                continue;
            }
            while (j < 2) {
                j++;

                int newCol = start.getColumn() + col;
                if (outOfBounds(newCol)) {
                    col += 2 * colMove;
                    continue;
                }
                ChessPosition end = new ChessPosition(newRow, newCol);
                if (board.getPiece(end) == null || board.getPiece(end).getTeamColor() != color) {
                    validMoves.add(new ChessMove(start, end, null));
                    if (recursive && (board.getPiece(end) == null)) {
                        recursiveCalculator(row, col, end);
                    }

                }
                col += 2 * colMove;
            }
            row += 2 * rowMove;
        }
    }

    private void recursiveCalculator(int rowMove, int colMove, ChessPosition here){
        int newRow = here.getRow() + rowMove;
        int newCol = here.getColumn() + colMove;
        if (!outOfBounds(newRow) && !outOfBounds(newCol)){
            ChessPosition newMove = new ChessPosition(newRow, newCol);
            if (board.getPiece(newMove) == null) {
                validMoves.add(new ChessMove(start, newMove, null));
                recursiveCalculator(rowMove, colMove, newMove);
            } else if (board.getPiece(newMove).getTeamColor() != color){
                validMoves.add(new ChessMove(start, newMove, null));
            }
        }
    }

    private void pawnPromotionCheck(int lastRow, ChessPosition newPosition) {
        if (newPosition.getRow() == lastRow){
            validMoves.add(new ChessMove(start, newPosition, ChessPiece.PieceType.KNIGHT));
            validMoves.add(new ChessMove(start, newPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(start, newPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(start, newPosition, ChessPiece.PieceType.ROOK));
        } else {
            validMoves.add(new ChessMove(start, newPosition, null));
        }
    }

    private void pawnMoveCalculator() {
        int forwards;
        int firstMove = switch (color) {
            case BLACK -> {
                forwards = -1;
                yield 6;
            }
            case WHITE -> {
                forwards = 1;
                yield 3;
            }
        };
        int newRow = start.getRow() + forwards;
        ChessPosition newPosition = new ChessPosition(newRow, start.getColumn());
        if (board.getPiece(newPosition) == null) {
            pawnPromotionCheck(firstMove + 5 * forwards, newPosition);
            if (newRow == firstMove) {
                newPosition = new ChessPosition(newRow + forwards, start.getColumn());
                if (board.getPiece(newPosition) == null) {
                    validMoves.add(new ChessMove(start, newPosition, null));
                }
            }
        }
        for (int i = -1; i <= 1; i+=2){
            int newCol = start.getColumn() + i;
            if (!outOfBounds(newCol)) {
                newPosition = new ChessPosition(newRow, newCol);
                if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != color) {
                    pawnPromotionCheck(firstMove + 5 * forwards, newPosition);
                }
            }
        }
    }

    public Collection<ChessMove> getValidMoves(){
        return validMoves;
    }
}
