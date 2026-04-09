package server.websocket;

import chess.ChessGame;
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
import java.util.Collection;
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
    public void handleMessage(@NotNull WsMessageContext ctx) {
        //the server recieves the message and decides what to do with it
        try {
            UserGameCommand userGameCommand = Serializer.fromJson(ctx.message(), UserGameCommand.class);
            MoveCommand moveCommand = Serializer.fromJson(ctx.message(), MoveCommand.class);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand, ctx.session);
                case LEAVE -> leave(userGameCommand, ctx.session);
                case MAKE_MOVE -> makeMove(moveCommand, ctx.session);
                case RESIGN -> resign(userGameCommand, ctx.session);
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
        String username = authDAO.getUserByToken(userGameCommand.getAuthToken());
        if (username == null) {
            String message = "Error: Not Authorized.";
            var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(Serializer.toJson(errorMsg));
        } else {
            GameData game = gameDAO.getGameByID(userGameCommand.getGameID());
            if (game == null) {
                String message = "Error: Game not found.";
                var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
                session.getRemote().sendString(Serializer.toJson(errorMsg));
            } else {
                PlayerInfo playerInfo;
                if (userGameCommand.getPlayerType() == null) {
                    GameData updatedGame = getUpdatedGame(game, username);
                    gameDAO.updateGameData(updatedGame);
                    game = updatedGame;
                    String playerType;
                    if (username.equals(game.getWhiteUsername())) {
                        playerType = "WHITE";
                    } else if (username.equals(game.getBlackUsername())) {
                        playerType = "BLACK";
                    } else {
                        playerType = "OBSERVER";
                    }
                    playerInfo = new PlayerInfo(username, userGameCommand.getGameID(), playerType);
                } else {
                    playerInfo = new PlayerInfo(username, userGameCommand.getGameID(), userGameCommand.getPlayerType());
                }
                connections.add(userGameCommand.getGameID(), session, playerInfo);
                String playerType = userGameCommand.getPlayerType();
                var loadGameMsg = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game(), playerType);
                session.getRemote().sendString(Serializer.toJson(loadGameMsg));
                String message = String.format(" %s has joined the game as %s", username, playerType);
                var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(userGameCommand.getGameID(), session, notification);
            }
        }
    }

    @NotNull
    private static GameData getUpdatedGame(GameData game, String username) {
        String whiteUsername = game.getWhiteUsername();
        String blackUsername = game.getBlackUsername();
        if (game.getWhiteUsername() == null && !username.equals("observer")) {
            whiteUsername = username;
        } else if (game.getBlackUsername() == null && !username.equals("observer")) {
            blackUsername = username;
        }
        return new GameData(game.gameId(), whiteUsername, blackUsername, game.gameName(), game.game());
    }

    public void leave(UserGameCommand userGameCommand, Session session) throws ResponseException, IOException {
        connections.remove(userGameCommand.getGameID(), session);
        String username = authDAO.getUserByToken(userGameCommand.getAuthToken());
        if (Objects.equals(userGameCommand.getPlayerType(), "WHITE") || Objects.equals(userGameCommand.getPlayerType(), "BLACK")) {
            GameService.leaveGame(userGameCommand.getGameID(), username, userGameCommand.getPlayerType());
        } else if (userGameCommand.getPlayerType()==null) {
            GameData game = gameDAO.getGameByID(userGameCommand.getGameID());
            String playerType;
            if (username.equals(game.getWhiteUsername())) {
                playerType = "WHITE";
                GameService.leaveGame(userGameCommand.getGameID(), username,playerType);
            } else if (username.equals(game.getBlackUsername())) {
                playerType = "BLACK";
                GameService.leaveGame(userGameCommand.getGameID(), username,playerType);
            }
            gameDAO.updateGameData(game);
//            GameData updated = gameDAO.getGameByID(game.gameId());

        }
        String message = String.format("   %s has left the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(userGameCommand.getGameID(), session, notification);
    }

    public void resign(UserGameCommand userGameCommand, Session session) throws ResponseException, IOException {
        PlayerInfo player = connections.getPlayer(userGameCommand.getGameID(), session);
        String playerColor = player.getPlayerType();
        if (Objects.equals(playerColor, "OBSERVER")) {
            String nullError = "Error: Observers can't resign.";
            var nullErrorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, nullError);
            session.getRemote().sendString(Serializer.toJson(nullErrorMsg));
        } else {
            String opponentColor = (Objects.equals(playerColor, "WHITE")) ? "BLACK" : "WHITE";
            String opponentName = getOpponentName(userGameCommand, opponentColor);


            String username = authDAO.getUserByToken(userGameCommand.getAuthToken());
            GameData game = gameDAO.getGameByID(userGameCommand.getGameID());
            if (game.game().getIsGameOver()) {
                String message = "Error: Game is already over. Please leave the game to exit.";
                var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
                session.getRemote().sendString(Serializer.toJson(error));
            } else {
                game.game().setGameOver();
                gameDAO.updateGameData(game);
                String message = String.format("   %s has resigned. %s wins!", username, opponentName);
                var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcastToAll(userGameCommand.getGameID(), notification);
            }
            connections.remove(userGameCommand.getGameID(), session);
        }
    }

    private String getOpponentName(UserGameCommand userGameCommand, String opponentColor) {
        String opponentName = opponentColor;
        Collection<PlayerInfo> allPlayers = connections.getAllPlayers(userGameCommand.getGameID());
        for (PlayerInfo selectedPlayer : allPlayers) {
            if (Objects.equals(selectedPlayer.getPlayerType(), opponentColor)) {
                opponentName = selectedPlayer.getUsername();
            }
        }
        return opponentName;
    }

    public void makeMove(MoveCommand moveCommand, Session session) throws ResponseException, IOException {
        ChessMove move = moveCommand.getMove();
        String username = authDAO.getUserByToken(moveCommand.getAuthToken());
        if (username == null) {
            String message = "Error: Not Authorized.";
            var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(Serializer.toJson(errorMsg));
        } else {
            String message = makeMoveNotification(move, username);

            GameData game = gameDAO.getGameByID(moveCommand.getGameID());
            PlayerInfo player = connections.getPlayer(game.gameId(), session);
            if (player == null) {
                String nullError = "Error: Game is over. No more moves can be made. Please leave the game to exit.";
                var nullErrorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, nullError);
                session.getRemote().sendString(Serializer.toJson(nullErrorMsg));
            } else {
                String playerType = player.getPlayerType(); //"WHITE"
                if (Objects.equals(playerType, "OBSERVER")) {
                    String nullError = "Error: Observers can't make moves.";
                    var nullErrorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, nullError);
                    session.getRemote().sendString(Serializer.toJson(nullErrorMsg));
                } else {
                    ChessGame.TeamColor playerColor = getTeamColor(playerType); //.WHITE
                    ChessGame.TeamColor currentTeam = game.game().getTeamTurn(); //.WHITE
                    ChessGame.TeamColor opponentColor = (playerColor == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE; //.BLACK
                    String opponentColorString = (Objects.equals(playerType, "WHITE")) ? "BLACK" : "WHITE"; // "BLACK"
                    String opponentName = getOpponentName(moveCommand, opponentColorString);
                    if (!game.game().getIsGameOver()) {
                        if (playerColor != null && playerColor != currentTeam) {
                            String error = "Error: Please Wait For Your Turn";
                            var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, error);
                            session.getRemote().sendString(Serializer.toJson(errorMsg));
                            return;
                        }
                    }

                    try {
                        game.game().makeMove(move);
                        game = gameDAO.updateGameData(game);
//                String opponentName = getOpponentName(opponentColor, game);
                        if (game.game().isInCheckmate(opponentColor)) {
                            String checkMessage = String.format("   Checkmate! %s wins!", username);
                            var checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);
                            var loadGameMsg = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game(), playerType);
                            connections.broadcast(game.gameId(), null, loadGameMsg);
                            connections.broadcast(game.gameId(), null, checkNotification);
                            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                            connections.broadcast(moveCommand.getGameID(), session, notification);
                        } else if (game.game().isInStalemate(opponentColor)) {
                            String checkMessage = "   Stalemate! Game is a draw.";
                            var checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);
                            connections.broadcast(game.gameId(), null, checkNotification);
                        } else if (game.game().isInCheck(opponentColor)) {
                            String checkMessage = String.format("   %s is in check", opponentName);
                            var checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);
                            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                            connections.broadcastUpdateToAll(moveCommand.getGameID(), game);
                            connections.broadcast(game.gameId(), null, checkNotification);
                            connections.broadcast(moveCommand.getGameID(), session, notification);
                        } else {
                            connections.broadcastUpdateToAll(moveCommand.getGameID(), game);
                            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                            connections.broadcast(moveCommand.getGameID(), session, notification);
                        }

                    } catch (InvalidMoveException e) {
                        var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
                        session.getRemote().sendString(Serializer.toJson(errorMsg));
//                throw new InvalidMoveException(e.getMessage());
                    } catch (ResponseException e) {
                        var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
                        session.getRemote().sendString(Serializer.toJson(errorMsg));
//                throw new ResponseException(ResponseException.Code.ServerError,e.getMessage());
                    }
                }
            }
        }

    }

    private static ChessGame.TeamColor getTeamColor(String playerType) {
        ChessGame.TeamColor playerColor;
        if (Objects.equals(playerType, "WHITE")) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(playerType, "BLACK")) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            playerColor = null;
        }
        return playerColor;
    }

//    private static String getPlayerColor(ChessGame.TeamColor playerType) {
//        String playerColor;
//        if (playerType == ChessGame.TeamColor.WHITE) {
//            playerColor = "WHITE";
//        } else {
//            playerColor = "BLACK";
//        }
//        return playerColor;
//    }


    @NotNull
    private static String makeMoveNotification(ChessMove move, String username) {
        ChessPosition startPos = move.getStartPosition();
        int startPosRow = startPos.getRow();
        int startPosCol = startPos.getColumn();
        char startPosColLetter = (char) ('A' + startPosCol - 1);
        ChessPosition endPos = move.getEndPosition();
        int endPosRow = endPos.getRow();
        int endPosCol = endPos.getColumn();
        char endPosColLetter = (char) ('A' + endPosCol - 1);
        return String.format("   %s has moved from %s%d to %s%d", username, startPosColLetter, startPosRow, endPosColLetter, endPosRow);
    }

}
