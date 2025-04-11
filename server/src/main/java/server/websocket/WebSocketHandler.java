package server.websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.GameData;
import model.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.Service;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static ui.EscapeSequences.RESET_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;


@WebSocket
public class WebSocketHandler {
    HashMap<Integer, ConnectionManager> rooms = new HashMap<>();
    Service service;

    public WebSocketHandler(Service service) {
        this.service = service;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws ResponseException {
        UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);
        switch (command.getCommandType()) {
            case LEAVE -> leave(command, session);
            case RESIGN -> resign(command, session);
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> {
                var moveCommand = new Gson().fromJson(msg, MoveCommand.class);
                move(moveCommand, session);
            }
        }
    }

    private void connect(UserGameCommand command, Session session) throws ResponseException {
        var authToken = command.getAuthToken();
        int gameID = command.getGameID();
        GameData game;
        try {
            game = service.getGame(authToken, gameID);
            if (game == null) {
                throw new ResponseException(500, "bad game");
            }
        } catch (ResponseException e) {
            error("Error getting game", session);
            return;
        }
        String userName = getUserName(authToken, session);
        var room = rooms.get(gameID);
        if (room == null) {
            room = new ConnectionManager();
            rooms.put(gameID, room);
        }
        room.add(userName, session);
        String joinType;
        if (Objects.equals(game.blackUsername(), userName)) {
            joinType = "BLACK";
        } else if (Objects.equals(game.whiteUsername(), userName)) {
            joinType = "WHITE";
        } else {
            joinType = "an observer";
        }
        String msg = SET_TEXT_COLOR_YELLOW + userName + RESET_COLOR + " has joined as " + SET_TEXT_COLOR_YELLOW + joinType
                + RESET_COLOR + ".";
        var message = new NotificationMessage(msg);
        var loadMessage = new LoadGameMessage(game);
        try {
            room.send(userName, loadMessage);
            room.broadcast(userName, message);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void leave(UserGameCommand command, Session session) throws ResponseException {
        try {
            var authToken = command.getAuthToken();
            int gameID = command.getGameID();
            GameData game;
            String userName = getUserName(authToken, session);
            try {
                game = service.getGame(authToken, gameID);
            } catch (ResponseException e) {
                error("Error while getting game data", session);
                return;
            }
            if (Objects.equals(game.whiteUsername(), userName)) {
                game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            }
            if (Objects.equals(game.blackUsername(), userName)) {
                game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            }
            service.updateGame(authToken, game);
            var room = rooms.get(gameID);
            room.remove(userName);
            String msg = SET_TEXT_COLOR_YELLOW + userName + RESET_COLOR + " has left the game.";
            var message = new NotificationMessage(msg);
            room.broadcast(userName, message);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void resign(UserGameCommand command, Session session) throws ResponseException {

        var authToken = command.getAuthToken();
        int gameID = command.getGameID();
        GameData game;
        try {
            game = service.getGame(authToken, gameID);
        } catch (ResponseException e) {
            error("Error while getting game data", session);
            return;
        }
        if (game.game().isGameWon()) {
            error("Error: cannot resign when the game is won", session);
            return;
        }
        String userName = getUserName(authToken, session);
        if (!Objects.equals(userName, game.blackUsername()) && !Objects.equals(userName, game.whiteUsername())) {
            error("Error: cannot resign as observer", session);
            return;
        }
        var room = rooms.get(gameID);
        game.game().setGameWon(true);
        try {
            service.updateGame(authToken, game);
        } catch (ResponseException e) {
            error("Error while updating game", session);
            return;
        }
        String msg = SET_TEXT_COLOR_YELLOW + userName + RESET_COLOR + " has resigned.";
        var message = new NotificationMessage(msg);
        try {
            notification(SET_TEXT_COLOR_YELLOW + "You have resigned." + RESET_COLOR, session);
            room.broadcast(userName, message);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void move(MoveCommand command, Session session) throws ResponseException {
        int gameID = command.getGameID();
        var authToken = command.getAuthToken();
        GameData game;
        try {
            game = service.getGame(authToken, gameID);
        } catch (ResponseException e) {
            error("Error while getting game", session);
            return;
        }
        var move = command.getMove();
        String userName = getUserName(authToken, session);
        try {
            var teamTurn = game.game().getTeamTurn();

            String expectedName = switch (teamTurn) {
                case BLACK -> game.blackUsername();
                case WHITE -> game.whiteUsername();
            };

            if (Objects.equals(userName, expectedName)) {
                game.game().makeMove(move);
            } else {
                throw new InvalidMoveException();
            }
        } catch (InvalidMoveException e) {
            error("Error: invalid move", session);
            return;
        }
        try {
            service.updateGame(authToken, game);
        } catch (ResponseException e) {
            error("Error while updating game", session);
            return;
        }

        var room = rooms.get(gameID);
        String msg = SET_TEXT_COLOR_YELLOW + userName + RESET_COLOR + " has moved to " + SET_TEXT_COLOR_YELLOW + move.getEndPosition().toString() + RESET_COLOR;
        var message = new NotificationMessage(msg);
        var loadMessage = new LoadGameMessage(game);
        var teamTurn = game.game().getTeamTurn();
        try {
            room.broadcast(null, loadMessage);
            room.broadcast(userName, message);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
        String nextUser = switch (teamTurn) {
            case WHITE -> game.whiteUsername();
            case BLACK -> game.blackUsername();
        };
        if (game.game().isInStalemate(teamTurn)) {
            game.game().setGameWon(true);
            String staleMsg = SET_TEXT_COLOR_YELLOW + nextUser + RESET_COLOR + "is in stalemate!";
            NotificationMessage staleMessage = new NotificationMessage(staleMsg);
            String personalStaleMsg = SET_TEXT_COLOR_YELLOW + "You are in stalemate!" + RESET_COLOR;
            NotificationMessage personalStaleMessage = new NotificationMessage(personalStaleMsg);
            try {
                room.broadcast(nextUser, staleMessage);
                room.send(nextUser, personalStaleMessage);
                service.updateGame(authToken, game);

            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            } catch (ResponseException e) {
                error("Error while updating game", session);
            }

        } else if (game.game().isInCheck(teamTurn)) {
            String checkMsg;
            NotificationMessage checkMessage;
            String personalCheckMsg;
            NotificationMessage personalCheckMessage;
            if (game.game().isInCheckmate(teamTurn)) {
                checkMsg = SET_TEXT_COLOR_YELLOW + nextUser + RESET_COLOR + " has been checkmated!";
                personalCheckMsg = SET_TEXT_COLOR_YELLOW + "You have been checkmated!" + RESET_COLOR;
            } else {
                checkMsg = SET_TEXT_COLOR_YELLOW + nextUser + RESET_COLOR + " is in check!";
                personalCheckMsg = SET_TEXT_COLOR_YELLOW + "You are in check!" + RESET_COLOR;
            }
            checkMessage = new NotificationMessage(checkMsg);
            personalCheckMessage = new NotificationMessage(personalCheckMsg);
            try {
                room.broadcast(nextUser, checkMessage);
                room.send(nextUser, personalCheckMessage);
                service.updateGame(authToken, game);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            } catch (ResponseException e) {
                error("Error while updating game", session);
            }
        }

    }

    private String getUserName(String authToken, Session session) throws ResponseException {
        try {
            var authData = service.verifyAuthData(authToken);
            return authData.username();
        } catch (ResponseException e) {
            error("Error: unauthorized", session);
            return "";
        }
    }

    private void error(String msg, Session session) throws ResponseException {
        try {
            var message = new ErrorMessage(msg);
            session.getRemote().sendString(message.toString());
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void notification(String msg, Session session) throws ResponseException {
        try {
            var message = new NotificationMessage(msg);
            session.getRemote().sendString(message.toString());
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
