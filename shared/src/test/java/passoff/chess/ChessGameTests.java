package passoff.chess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChessGameTests {
    @Test
    @DisplayName("Highlight Print Test")
    public void printTest(){
        var location = new ChessPosition(2,2);
        var game = new ChessGame();
        System.out.println(game.projectValidMoves(location, ChessGame.TeamColor.WHITE));
        var board = new ChessBoard();
        var left = new ChessPosition(1,1);
        var right = new ChessPosition(8,8);
        board.addPiece(left, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        board.addPiece(right, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        game.setBoard(board);
        System.out.println(game.projectValidMoves(left, ChessGame.TeamColor.WHITE));
    }
}
