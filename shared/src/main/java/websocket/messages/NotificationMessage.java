package websocket.messages;

public class NotificationMessage extends ServerMessage{
    String message;
    public NotificationMessage(ServerMessageType type, String msg) {
        super(type);
        this.message = msg;
    }
}
