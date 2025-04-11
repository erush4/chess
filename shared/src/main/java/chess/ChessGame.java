package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

import static ui.EscapeSequences.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    private ChessMove lastMove;
    private boolean gameWon;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        lastMove = null;
        gameWon = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board) && Objects.equals(lastMove, chessGame.lastMove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, lastMove);
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

        for (Iterator<ChessMove> iterator = validMoves.iterator(); iterator.hasNext(); ) {
            ChessMove move = iterator.next();
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece spaceCapture = board.getPiece(endPosition);
            //tests the move
            board.addPiece(startPosition, null);
            board.addPiece(endPosition, piece);
            if (isInCheck(color)) {
                iterator.remove();
            }
            //reverts back
            board.addPiece(startPosition, piece);
            board.addPiece(endPosition, spaceCapture);
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
        if (gameWon) {
            throw new InvalidMoveException();
        }
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null || piece.getTeamColor() != teamTurn) { //only move on your turn
            throw new InvalidMoveException();
        }
        if (validMoves(startPosition).contains(move)) { //checks to make sure move is valid
            if (move.getPromotionPiece() == null) { //normal move
                board.addPiece(move.getEndPosition(), piece);
            } else { //pawn promotion
                ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotionPiece);
            }
            board.addPiece(startPosition, null);
            teamTurn = switch (teamTurn) {
                case WHITE -> TeamColor.BLACK;
                case BLACK -> TeamColor.WHITE;
            };
            lastMove = move;
        } else { //if move is not in validMoves
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition newPosition = new ChessPosition(i, j); //gets every position on the board
                ChessPiece piece = board.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    if (threats(newPosition, teamColor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean threats(ChessPosition newPosition, TeamColor teamColor) {
        for (ChessMove threat : board.getPiece(newPosition).pieceMoves(board, newPosition)) {
            ChessPiece possibleCapture = board.getPiece(threat.getEndPosition());
            boolean kingThreatened = possibleCapture != null && possibleCapture.getPieceType() == ChessPiece.PieceType.KING;
            if (kingThreatened && possibleCapture.getTeamColor() == teamColor) { //king threatened (same color)
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && noMoves(teamColor);
    }

    private boolean noMoves(TeamColor teamColor) {
        boolean stalemate = true;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition newPosition = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(newPosition).isEmpty()) {
                        stalemate = false;
                    }
                }
            }
        }
        return stalemate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return noMoves(teamColor) && !isInCheck(teamColor);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }

    public String projectValidMoves(ChessPosition position, TeamColor team) {
        var moves = validMoves(position);
        HashSet<ChessPosition> spaces = new HashSet<>();
        for (ChessMove m : moves) {
            spaces.add(m.getEndPosition());
        }
        StringBuilder string = new StringBuilder();
        string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY);
        switch (team) {
            case BLACK -> {
                for (int i = 0; i < 7; i++) {
                    string.append(" ").append((char) ('h' - i)).append("\u2003");
                }
                string.append(" a " + EMPTY + RESET_COLOR + "\n");
                boolean isDark = false;
                for (int row = 1; row <= 8; row++) {
                    string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
                    string.append(" ").append(row).append(" ");
                    isDark = !isDark;
                    for (int col = 8; col >= 1; col--) {
                        isDark = squareToString(string, isDark, row, col, spaces);
                    }
                    string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
                    string.append(" ").append(row).append("\u2003").append(RESET_COLOR).append("\n");
                }
                string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY);
                for (int i = 0; i < 7; i++) {
                    string.append(" ").append((char) ('h' - i)).append("\u2003");
                }
                string.append(" a " + EMPTY + RESET_COLOR + "\n");
            }
            case WHITE -> {
                for (int col = 0; col < 7; col++) {
                    string.append(" ").append((char) ('a' + col)).append("\u2003");
                }
                string.append(" h " + EMPTY + RESET_COLOR + "\n");

                boolean isDark = false;
                for (int row = 8; row >= 1; row--) {
                    string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
                    string.append(" ").append(row).append(" ");
                    isDark = !isDark;
                    for (int col = 1; col <= 8; col++) {
                        isDark = squareToString(string, isDark, row, col, spaces);
                    }
                    string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK);
                    string.append(" ").append(row).append("\u2003").append(RESET_COLOR).append("\n");
                }
                string.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY);
                for (int col = 0; col < 7; col++) {
                    string.append(" ").append((char) ('a' + col)).append("\u2003");
                }
                string.append(" h " + EMPTY + RESET_COLOR + "\n");
            }
        }
        return string.toString();
    }

    private boolean squareToString(StringBuilder string, boolean isDark, int row, int col, HashSet<ChessPosition> selected) {
        var location = new ChessPosition(row, col);
        String squareColor;
        if (isDark) {
            if (selected.contains(location)) {
                squareColor = SET_BG_COLOR_CYAN;
            } else {
                squareColor = SET_BG_COLOR_DARK_GREEN;
            }
        } else {
            if (selected.contains(location)){
                squareColor = SET_BG_COLOR_BRIGHT_CYAN;
            } else{
                squareColor = SET_BG_COLOR_LIGHT_GREY;
            }
        }
        string.append(squareColor);

        var piece = board.getPiece(location);
        if (piece == null) {
            string.append(EMPTY);
        } else {
            string.append(piece);
        }
        return !isDark;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
}
