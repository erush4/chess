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
        String msg = userName + " has joined as " + joinType;
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

            String userName = getUserName(authToken, session);
            var room = rooms.get(gameID);
            room.remove(userName);
            String msg = userName + " has left the game";
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
        }
        String userName = getUserName(authToken, session);
        if (!Objects.equals(userName, game.blackUsername()) && !Objects.equals(userName, game.whiteUsername())){
            error("Error: cannot resign as observer", session);
        }
        var room = rooms.get(gameID);
        game.game().setGameWon(true);
        try {
            service.updateGame(authToken, game);
        } catch (ResponseException e) {
            error("Error while updating game", session);
        }
        String msg = userName + " has resigned";
        var message = new NotificationMessage(msg);
        try {
            notification(msg, session);
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
        String msg = userName + " has moved to " + move.getEndPosition();
        var message = new NotificationMessage(msg);
        var loadMessage = new LoadGameMessage(game);
        try {
            room.broadcast(null, loadMessage);
            room.broadcast(userName, message);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
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
