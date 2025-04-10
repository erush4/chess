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
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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
    public void onMessage(Session session, String msg) {
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

    private void connect(UserGameCommand command, Session session) {
        var authToken = command.getAuthToken();
        int gameID = command.getGameID();
        GameData game;
        try {
            game = service.getGame(authToken, gameID);
        } catch (ResponseException e) {
            error("Error getting game", session);
            return;
        }
        String userName = getUserName(authToken, session);
        var connections = rooms.get(gameID);
        if (connections == null) {
            connections = new ConnectionManager();
            rooms.put(gameID, connections);
        }
        connections.add(userName, session);
        String joinType;
        if (Objects.equals(game.blackUsername(), userName)) {
            joinType = "BLACK";
        } else if (Objects.equals(game.whiteUsername(), userName)) {
            joinType = "WHITE";
        } else {
            joinType = "an observer";
        }

        String msg = userName + " has joined as " + joinType;
        var message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        try {
            connections.broadcast(userName, message);
        } catch (IOException ignored) {
        }
    }

    private void leave(UserGameCommand command, Session session) {
        try {
            var authToken = command.getAuthToken();
            int gameID = command.getGameID();

            String userName = getUserName(authToken, session);
            var room = rooms.get(gameID);
            room.remove(userName);
            String msg = userName + " has left the game";
            var message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
            room.broadcast(userName, message);
        } catch (IOException ignored) {
        }
    }

    private void resign(UserGameCommand command, Session session) {
        var authToken = command.getAuthToken();
        int gameID = command.getGameID();
        GameData game;
        try {
            game = service.getGame(authToken, gameID);
        } catch (ResponseException e) {
            error("Error while getting game data", session);
            return;
        }
        String userName = getUserName(authToken, session);
        var connections = rooms.get(gameID);
        game.game().setGameWon(true);
        try {
            service.updateGame(authToken, game);
        } catch (ResponseException e) {
            error("Error while updating game", session);
        }
        String msg = userName + " has resigned";
        var message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        try {
            connections.broadcast(userName, message);
        } catch (IOException ignored) {
        }
    }

    private void move(MoveCommand command, Session session) {
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
        try {
            game.game().makeMove(move);
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
        var connections = rooms.get(gameID);
        String userName = getUserName(authToken, session);
        String msg = userName + " has moved to " + move.getEndPosition();
        var message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        try {
            connections.broadcast(userName, message);
        } catch (IOException ignored) {
        }
    }

    private String getUserName(String authToken, Session session) {
        try {
            var authData = service.verifyAuthData(authToken);
            return authData.username();
        } catch (ResponseException e) {
            error("Error: unauthorized", session);
            return "";
        }
    }

    private void error(String msg, Session session) {
        try {
            var message = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
            session.getRemote().sendString(message.toString());
        } catch (IOException ignored) {
        }
    }
}
