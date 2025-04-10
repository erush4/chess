package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add (String userName, Session session){
        var connection = new Connection(userName, session);
        connections.put(userName, connection); //later, see if you can change this to just be the session
    }

    public void broadcast(String excludeUserName, ServerMessage msg) throws IOException {
        var closedConnections = new ArrayList<Connection>();
        for (var c : connections.values()){
            if (c.session.isOpen()){
                if (!c.userName.equals(excludeUserName)){
                    c.send(msg.toString());
                }
            } else {
                closedConnections.add(c);
            }
        }
        for (var c : closedConnections){
            remove(c.userName);
        }
    }

    public void remove(String userName){
        connections.remove(userName);
    }
}
