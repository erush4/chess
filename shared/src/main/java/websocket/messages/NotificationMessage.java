package websocket.messages;

public class NotificationMessage extends ServerMessage {
    String message;

    public NotificationMessage(String msg) {
        super(ServerMessageType.NOTIFICATION);
        this.message = msg;
    }
}
