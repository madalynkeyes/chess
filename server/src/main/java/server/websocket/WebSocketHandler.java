package server.websocket;

import dataaccess.AuthDAO;
import io.javalin.websocket.*;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.Serializer;
import websocket.commands.UserGameCommand;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
        @Override
        public void handleConnect(@NotNull WsConnectContext ctx) {
            ctx.enableAutomaticPings();
            System.out.println("Websocket connected");
        }

        @Override
        public void handleMessage(@NotNull WsMessageContext ctx) {
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

        public void connect(UserGameCommand userGameCommand, Session session){
            //I need some way to validate the auth token and get the username so I can send a notification
            connections.add(userGameCommand.getGameID(), session, userGameCommand.getAuthToken());
            System.out.println("hahahha");
//            var game = gameService.getGame(userGameCommand.getGameID());
//            var msg = new LoadGameMessage(game);
//
//            session.getRemote().sendString(gson.toJson(msg));
//            var notification = new NotificationMessage(username + " joined the game");
//            connections.broadcast(gameID, session, notification);

        }

}
