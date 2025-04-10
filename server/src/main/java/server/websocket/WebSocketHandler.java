package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.Service;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.HashMap;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    HashMap<Integer, ConnectionManager> gameConnections = new HashMap<>();
    Service service;

    public WebSocketHandler(Service service){
        this.service = service;
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws ResponseException {
        UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);
        switch (command.getCommandType()){
            case LEAVE -> leave(command, session);
            case RESIGN -> resign(command, session);
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> move(command, session);
        }

    }

    private void leave(UserGameCommand command, Session session) {

    }

    private void resign(UserGameCommand command, Session session) {
    }

    private void move(UserGameCommand command, Session session) {
    }

    private void connect(UserGameCommand command, Session session) throws ResponseException {

    }
}
