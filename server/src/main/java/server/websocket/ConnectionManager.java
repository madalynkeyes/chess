package server.websocket;
import org.eclipse.jetty.websocket.api.Session;
import server.Serializer;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    ConcurrentHashMap<Integer, ConcurrentHashMap<Session, String>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session, String username) {
        connections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        connections.get(gameID).put(session, username);
    }

    public void remove(int gameID, Session session) {
        var gameConnections = connections.get(gameID);
        if (gameConnections != null) {
            gameConnections.remove(session);
        }
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        var gameConnections = connections.get(gameID);

        if (gameConnections == null) return;

        for (Session c : gameConnections.keySet()) {
            if (c.isOpen() && !c.equals(excludeSession)) {
                c.getRemote().sendString(Serializer.toJson(message));
            }
        }
        //this loops through all the connected clients in the session, skips the sender and sends the message to everyone else
        //--> goes to WebSocketFacade.addMessageHandler
    }

    public void broadcastToAll(int gameID, ServerMessage message) throws IOException {
        var gameConnections = connections.get(gameID);
        System.out.println("The game connections is:" + gameConnections);
        for (Session c : gameConnections.keySet()) {
            System.out.println("key:"+gameConnections.keySet());
            System.out.println("c"+c);
            System.out.println(message.toString());
            System.out.println(Serializer.toJson(message));
            if (c.isOpen()) {
                c.getRemote().sendString(Serializer.toJson(message));
            }
        }
    }
}
