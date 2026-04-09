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
    private static ChessGame.TeamColor playerColor;
    private static ChessGame.TeamColor currentTeam;
    private ChessGame.TeamColor opponentColor;
    private String opponentName;
    private String username;
    private static GameData game;
    private String playerType;

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
        GameData game = gameDAO.getGameByID(userGameCommand.getGameID());
        GameData updatedGame = setColorToNull(username, game);
        if (Objects.equals(userGameCommand.getPlayerType(), "WHITE") || Objects.equals(userGameCommand.getPlayerType(), "BLACK")) {
            GameService.leaveGame(userGameCommand.getGameID(), username, userGameCommand.getPlayerType());
        }
            else if (userGameCommand.getPlayerType()==null) {
            String playerType;
            if (username.equals(game.getWhiteUsername())) {
                playerType = "WHITE";
                GameService.leaveGame(userGameCommand.getGameID(), username, playerType);
            } else if (username.equals(game.getBlackUsername())) {
                playerType = "BLACK";
                GameService.leaveGame(userGameCommand.getGameID(), username, playerType);
            }
        }
        gameDAO.updateGameData(updatedGame);
        String message = String.format("   %s has left the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(userGameCommand.getGameID(), session, notification);
    }

    @NotNull
    private static GameData setColorToNull(String username, GameData game) {
        GameData updatedGame;

        if (username.equals(game.getWhiteUsername())) {
            updatedGame = new GameData(
                    game.gameId(),
                    null,
                    game.getBlackUsername(),
                    game.gameName(),
                    game.game()
            );
        } else if (username.equals(game.getBlackUsername())) {
            updatedGame = new GameData(
                    game.gameId(),
                    game.getWhiteUsername(),
                    null,
                    game.gameName(),
                    game.game()
            );
        } else {
            updatedGame = game; // observer, no change
        }
        return updatedGame;
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
        username = authDAO.getUserByToken(moveCommand.getAuthToken());
        if (username == null) {
            String message = "Error: Not Authorized.";
            var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(Serializer.toJson(errorMsg));
        } else {
            String message = makeMoveNotification(move, username);

            game = gameDAO.getGameByID(moveCommand.getGameID());
            PlayerInfo player = connections.getPlayer(game.gameId(), session);
            if (player == null) {
                String nullError = "Error: Game is over. No more moves can be made. Please leave the game to exit.";
                var nullErrorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, nullError);
                session.getRemote().sendString(Serializer.toJson(nullErrorMsg));
            } else {
                //"WHITE"
                playerType = player.getPlayerType();
                if (Objects.equals(playerType, "OBSERVER")) {
                    String nullError = "Error: Observers can't make moves.";
                    var nullErrorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, nullError);
                    session.getRemote().sendString(Serializer.toJson(nullErrorMsg));
                } else {
                    //.WHITE
                    playerColor = getTeamColor(playerType);
                    //.WHITE
                    currentTeam = game.game().getTeamTurn();
                    //.BLACK
                    opponentColor = (playerColor == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                    // "BLACK"
                    String opponentColorString = (Objects.equals(playerType, "WHITE")) ? "BLACK" : "WHITE";
                    opponentName = getOpponentName(moveCommand, opponentColorString);
                    if (notYourTurn(session)) {
                        return;
                    }

                    tryMakeMove(moveCommand, session, move,message);
                }
            }
        }

    }

    private void tryMakeMove(MoveCommand moveCommand, Session session,  ChessMove move,String message) throws IOException {
        try {
            game.game().makeMove(move);
            game = gameDAO.updateGameData(game);
            if (game.game().isInCheckmate(opponentColor)) {
                String checkMessage = String.format("   Checkmate! %s wins!", username);
                endGameNotifications(moveCommand, session, message, checkMessage);
            } else if (game.game().isInStalemate(opponentColor)) {
                String checkMessage = "   Stalemate! Game is a draw.";
                endGameNotifications(moveCommand, session, message, checkMessage);
            } else if (game.game().isInCheck(opponentColor)) {
                String checkMessage = String.format("   %s is in check", opponentName);
                endGameNotifications(moveCommand, session, message, checkMessage);
            } else {
                connections.broadcastUpdateToAll(moveCommand.getGameID(), game);
                var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(moveCommand.getGameID(), session, notification);
            }

        } catch (InvalidMoveException | ResponseException e) {
            var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(Serializer.toJson(errorMsg));
        }
    }

    private void endGameNotifications(MoveCommand moveCommand, Session session, String message, String checkMessage) throws IOException {
        var checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastUpdateToAll(moveCommand.getGameID(), game);
        connections.broadcast(game.gameId(), null, checkNotification);
        connections.broadcast(moveCommand.getGameID(), session, notification);
    }

    private static boolean notYourTurn(Session session) throws IOException {
        if (!game.game().getIsGameOver()) {
            if (playerColor != null && playerColor != currentTeam) {
                String error = "Error: Please Wait For Your Turn";
                var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, error);
                session.getRemote().sendString(Serializer.toJson(errorMsg));
                return true;
            }
        }
        return false;
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
