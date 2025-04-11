package server;

import model.ResponseException;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    NotificationHandler notificationHandler;

    public WebSocketFacade(NotificationHandler handler) throws ResponseException {
        try {
            URI socketURI = new URI("ws://localhost:8080");
            this.notificationHandler = handler;
        } catch (URISyntaxException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
