package server.websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.Service;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    HashMap<Integer, ConnectionManager> gameConnections = new HashMap<>();
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

    private void leave(UserGameCommand command, Session session) throws ResponseException {
        var authToken = command.getAuthToken();
        int gameID = command.getGameID();
        var authData = service.verifyAuthData(authToken);
        var userName = authData.username();
        var connections = gameConnections.get(gameID);
        connections.remove(userName);
        String msg = userName + " has left the game";
        var message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        try {
            connections.broadcast(userName, message);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void resign(UserGameCommand command, Session session) throws ResponseException {
        var authToken = command.getAuthToken();
        int gameID = command.getGameID();
        var game = service.getGame(authToken, gameID);
        var authData = service.verifyAuthData(authToken);
        var userName = authData.username();
        var connections = gameConnections.get(gameID);
        game.game().setGameWon(true);
        service.updateGame(authToken, game);
        String msg = userName + " has resigned";
        var message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        try {
            connections.broadcast(userName, message);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void move(MoveCommand command, Session session) throws ResponseException {
        var authToken = command.getAuthToken();
        int gameID = command.getGameID();
        var authData = service.verifyAuthData(authToken);
        var game = service.getGame(authToken, gameID);

        var move = command.getMove();
        try {
            game.game().makeMove(move);
        } catch (InvalidMoveException ignored) {
            return;
        }
        service.updateGame(authToken, game);
        var connections = gameConnections.get(gameID);
        String userName = authData.username();
        String msg = userName + " has moved to " + move.getEndPosition();
        var message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        try {
            connections.broadcast(userName, message);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void connect(UserGameCommand command, Session session) throws ResponseException {
        var authToken = command.getAuthToken();
        int gameID = command.getGameID();
        var authData = service.verifyAuthData(authToken);
        var game = service.getGame(authToken, gameID);
        String userName = authData.username();
        var connections = gameConnections.get(gameID);
        if (connections == null) {
            connections = new ConnectionManager();
            gameConnections.put(gameID, connections);
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
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
