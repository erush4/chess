import chess.ChessGame;
import chess.ChessPiece;
import ui.StartRepl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client:" + piece);

        new StartRepl("8080");
    }
}

