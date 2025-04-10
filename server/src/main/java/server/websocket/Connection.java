package server.websocket;

import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public String userName;
    public Session session;

    public Connection(String userName, Session session) {
        this.userName = userName;
        this.session = session;
    }

    public void send(String msg) throws IOException{
        session.getRemote().sendString(msg);
    }
}
