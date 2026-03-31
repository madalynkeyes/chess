package client.websocket;

import dataaccess.exceptions.ResponseException;
import jakarta.websocket.*;
import server.Serializer;
import ui.Client;
import ui.ClientChessBoard;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    UserGameCommand userGameCommand = Serializer.fromJson(message, UserGameCommand.class);
                    switch (userGameCommand.getCommandType()){
                        case CONNECT -> {}
                        case MAKE_MOVE -> {
                        }
                        case LEAVE -> {
                        }
                        case RESIGN -> {
                        }
                    }
                    System.out.println(userGameCommand);
//                    System.out.println(Serializer.fromJson(message));
//                    Notification notification = Serializer.fromJson(message, Notification.class);
//                    notificationHandler.notify(notification);
                    System.out.println("Something happensed in websocket facade?");
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void send(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    public void sendConnectMsg(String authToken, int gameID) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT,authToken,gameID);
            this.session.getBasicRemote().sendText(Serializer.toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
}
