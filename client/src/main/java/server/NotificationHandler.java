package server;

import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    default void notify(String serverMessage) {
        ServerMessage typeDeterminer = new Gson().fromJson(serverMessage, ServerMessage.class);
        switch (typeDeterminer.getServerMessageType()) {
            case NOTIFICATION -> notification(new Gson().fromJson(serverMessage, NotificationMessage.class));
            case ERROR -> error(new Gson().fromJson(serverMessage, ErrorMessage.class));
            case LOAD_GAME -> loadGame(new Gson().fromJson(serverMessage, LoadGameMessage.class));
        }
    }

    void notification(NotificationMessage message);

    void error(ErrorMessage message);

    void loadGame(LoadGameMessage message);
}
