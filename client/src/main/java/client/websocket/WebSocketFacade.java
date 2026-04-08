package client.websocket;

import chess.*;
import dataaccess.exceptions.ResponseException;
import jakarta.websocket.*;
import server.Serializer;
import ui.ClientChessBoard;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;
import static ui.EscapeSequences.*;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;
    static ChessBoard currentBoard;
    static String playerType;
    ChessGame currentGame;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            System.out.println("Connecting to: "+socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = Serializer.fromJson(message, ServerMessage.class);
                    if(serverMessage.getServerMessageType()== ServerMessage.ServerMessageType.NOTIFICATION) {
                        NotificationMessage notification = Serializer.fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(notification);
                    } else if (serverMessage.getServerMessageType()== ServerMessage.ServerMessageType.LOAD_GAME) {
                        LoadGameMessage loadGameMessage = Serializer.fromJson(message, LoadGameMessage.class);
                        playerType = loadGameMessage.getPlayerType();
                        currentBoard = loadGameMessage.getGameData().game().getBoard();
                        currentGame = loadGameMessage.getGameData().game();
                        drawBoard(currentBoard, playerType);
                    } else{
                        ErrorMessage errorMessage = Serializer.fromJson(message, ErrorMessage.class);
                        System.out.print(SET_TEXT_COLOR_RED);
                        System.out.println(errorMessage.getMessage());
                        System.out.print(RESET_TEXT_COLOR);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public static void drawBoard(ChessBoard board, String player) {
        if(board==null){
            board = currentBoard;
        }
        if(player==null){
            player=playerType;
        }
        System.out.println();
        ClientChessBoard.draw(board, player);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        //this method doesn't need anything inside it
    }


    public void sendConnectMsg(String authToken, int gameID, String playerType) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT,authToken,gameID,playerType);
            this.session.getBasicRemote().sendText(Serializer.toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void sendLeaveMsg(String authToken,int gameID,String playerType) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE,authToken,gameID,playerType);
            this.session.getBasicRemote().sendText(Serializer.toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void sendResignMsg(String authToken, int gameID, String playerType) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN,authToken,gameID,playerType);
            this.session.getBasicRemote().sendText(Serializer.toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void sendMoveMsg(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            var action = new MoveCommand(UserGameCommand.CommandType.MAKE_MOVE,authToken,gameID,playerType,move);
            this.session.getBasicRemote().sendText(Serializer.toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void highlightMoves(ChessPosition position,String playerType){
        Collection<ChessMove> moves = currentGame.validMoves(position);
        Set<ChessPosition> highlightSquares = ClientChessBoard.getHighlightSquare(moves);
        ClientChessBoard.drawHighlightBoard(currentBoard,position,highlightSquares,playerType);
    }
}
