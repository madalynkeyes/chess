package server.websocket;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.exceptions.ResponseException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.Serializer;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
        public void handleConnect(@NotNull WsConnectContext ctx) {
            ctx.enableAutomaticPings();
            System.out.println("Websocket connected");
        }

        @Override
        public void handleMessage(@NotNull WsMessageContext ctx) throws ResponseException, IOException {
            //the server recieves the message and decides what to do with it
            UserGameCommand userGameCommand = Serializer.fromJson(ctx.message(), UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand,ctx.session);
            }
        }

        @Override
        public void handleClose(@NotNull WsCloseContext ctx) {
            System.out.println("Websocket closed");
        }

        public void connect(UserGameCommand userGameCommand, Session session) throws ResponseException, IOException {
            //I need some way to validate the auth token and get the username so I can send a notification
            connections.add(userGameCommand.getGameID(), session, userGameCommand.getAuthToken());
            String username = authDAO.getUserByToken(userGameCommand.getAuthToken());
            GameData game = gameDAO.getGameByID(userGameCommand.getGameID());
            String playerType = userGameCommand.getPlayerType();

            var loadGameMsg = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,game);
            session.getRemote().sendString(Serializer.toJson(loadGameMsg));

            String message = String.format("%s has joined the game as %s",username,playerType);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
            connections.broadcast(userGameCommand.getGameID(), session,notification);

        }

}
