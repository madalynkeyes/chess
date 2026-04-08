package server.websocket;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.Serializer;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    ConcurrentHashMap<Integer, ConcurrentHashMap<Session, PlayerInfo>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session, PlayerInfo playerInfo) {
        connections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        connections.get(gameID).put(session, playerInfo);
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

    public void broadcastToAll(int gameID, NotificationMessage message) throws IOException {
        var gameConnections = connections.get(gameID);
        for (Session c : gameConnections.keySet()) {
            if (c.isOpen()) {
                c.getRemote().sendString(Serializer.toJson(message));
            }
        }
    }

    public void broadcastUpdateToAll(Integer gameID, GameData game) throws IOException {
        var gameConnections = connections.get(gameID);
        for (var entry : gameConnections.entrySet()) {
            Session session = entry.getKey();
            PlayerInfo player = entry.getValue();

            var msg = new LoadGameMessage(
                    ServerMessage.ServerMessageType.LOAD_GAME,
                    game,
                    player.getPlayerType()
            );

            session.getRemote().sendString(Serializer.toJson(msg));
        }
    }

    public PlayerInfo getPlayer(int gameID, Session session) {
        var gameConnections = connections.get(gameID);
        if (gameConnections == null){
            return null;
        }
        return gameConnections.get(session);
    }
}
