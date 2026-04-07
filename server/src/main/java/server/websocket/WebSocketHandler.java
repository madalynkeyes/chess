package server.websocket;

import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.exceptions.ResponseException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.Serializer;
import service.GameService;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
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
        public void handleMessage(@NotNull WsMessageContext ctx){
            //the server recieves the message and decides what to do with it
            try {
                UserGameCommand userGameCommand = Serializer.fromJson(ctx.message(), UserGameCommand.class);
                MoveCommand moveCommand = Serializer.fromJson(ctx.message(), MoveCommand.class);
                switch (userGameCommand.getCommandType()) {
                    case CONNECT -> connect(userGameCommand,ctx.session);
                    case LEAVE -> leave(userGameCommand,ctx.session);
                    case MAKE_MOVE -> makeMove(moveCommand,ctx.session);
                }
            } catch (Exception e) {
                System.out.println("WEBSOCKET ERROR:");
                e.printStackTrace();
            }
        }

        @Override
        public void handleClose(@NotNull WsCloseContext ctx) {
            System.out.println("Websocket closed");
        }

        public void connect(UserGameCommand userGameCommand, Session session) throws ResponseException, IOException {
            //I need some way to validate the auth token and get the username, so I can send a notification
            String username = authDAO.getUserByToken(userGameCommand.getAuthToken());
            PlayerInfo playerInfo = new PlayerInfo(username, userGameCommand.getGameID(), userGameCommand.getPlayerType());
            connections.add(userGameCommand.getGameID(), session, playerInfo);
            GameData game = gameDAO.getGameByID(userGameCommand.getGameID());
            String playerType = userGameCommand.getPlayerType();

            var loadGameMsg = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,game,playerType);
            session.getRemote().sendString(Serializer.toJson(loadGameMsg));

            String message = String.format("   %s has joined the game as %s",username,playerType);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
            connections.broadcast(userGameCommand.getGameID(),session,notification);

        }

        public void leave(UserGameCommand userGameCommand, Session session) throws ResponseException, IOException {
            connections.remove(userGameCommand.getGameID(), session);
            String username = authDAO.getUserByToken(userGameCommand.getAuthToken());
            if(Objects.equals(userGameCommand.getPlayerType(), "WHITE") || Objects.equals(userGameCommand.getPlayerType(), "BLACK")) {
                GameService.leaveGame(userGameCommand.getGameID(), username, userGameCommand.getPlayerType());
            }
            String message = String.format("   %s has left the game",username);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
            connections.broadcast(userGameCommand.getGameID(),session,notification);
        }

        public void makeMove(MoveCommand moveCommand,Session session) throws ResponseException, IOException, InvalidMoveException {
            ChessMove move = moveCommand.getMove();
            String username = authDAO.getUserByToken(moveCommand.getAuthToken());
            String message = makeMoveNotification(move, username);

            GameData game = gameDAO.getGameByID(moveCommand.getGameID());

            try {
                game.game().makeMove(move);
                game = gameDAO.updateGameData(game);

            } catch (InvalidMoveException e) {
                var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
                session.getRemote().sendString(Serializer.toJson(errorMsg));
                throw new InvalidMoveException(e.getMessage());
            } catch (ResponseException e) {
                var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
                session.getRemote().sendString(Serializer.toJson(errorMsg));
                throw new ResponseException(ResponseException.Code.ServerError,e.getMessage());
            }


            connections.broadcastUpdateToAll(moveCommand.getGameID(), game);

            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
            connections.broadcast(moveCommand.getGameID(),session,notification);
        }

    @NotNull
    private static String makeMoveNotification(ChessMove move, String username) {
        ChessPosition startPos = move.getStartPosition();
        int startPosRow = startPos.getRow();
        int startPosCol = startPos.getColumn();
        char startPosColLetter = (char)('A'+startPosCol-1);
        ChessPosition endPos = move.getEndPosition();
        int endPosRow = endPos.getRow();
        int endPosCol = endPos.getColumn();
        char endPosColLetter = (char)('A'+endPosCol-1);
        return String.format("   %s has moved from %s%d to %s%d", username,startPosColLetter,startPosRow,endPosColLetter,endPosRow);
    }

}
