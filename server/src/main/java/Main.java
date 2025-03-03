import chess.*;
import dataAccess.MemoryDataAccess;
import server.Server;
import service.Service;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        Server server = new Server(new Service(new MemoryDataAccess()));
        server.run(8080);
    }
}