package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard board;
    HashSet<ChessPosition> whiteThreatens;
    HashSet<ChessPosition> blackThreatens;
    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
    private void updatesThreats(TeamColor color){
        Collection<ChessPosition> threatens = switch (color) {
            case BLACK -> blackThreatens;
            case WHITE -> whiteThreatens;
        };
        threatens.clear();
        for(int i = 1; i <=8; i++){
            for (int j = 1; j <=8; j++){
                ChessPosition newPosition = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(newPosition);
                if (piece != null && piece.getTeamColor()!=color){
                    threatens.add(newPosition);
                }
            }
        }
    }
    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = piece.pieceMoves(board, startPosition);
        TeamColor color = piece.getTeamColor();
        TeamColor opponent;
        Collection<ChessPosition> threatens = switch (color) {
            case WHITE:
                opponent = TeamColor.BLACK;
                yield blackThreatens;
            case BLACK:
                opponent = TeamColor.WHITE;
                yield whiteThreatens;
        };
        if (threatens.contains(startPosition)) {
            for (Iterator<ChessMove> iterator = validMoves.iterator(); iterator.hasNext(); ) {
                ChessMove move =  iterator.next();
                ChessPosition endPosition = move.getEndPosition();
                //tests the move
                board.addPiece(startPosition, null);
                board.addPiece(endPosition, piece);
                updatesThreats(opponent);
                if (isInCheck(color)) {
                    iterator.remove();
                }
                //reverts back
                board.addPiece(startPosition, piece);
                board.addPiece(endPosition, null);
            }

        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingHere = board.findKing(teamColor);
        switch (teamColor) {
            case WHITE:
                if (kingHere == null || !blackThreatens.contains(kingHere)) {
                    return false;
                }
                break;
            case BLACK:
                if (kingHere == null || !whiteThreatens.contains(kingHere)) {
                    return false;
                }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && isInStalemate(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
