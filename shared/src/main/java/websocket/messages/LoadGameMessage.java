package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private final String playerType;
    private final ChessGame game;
    public LoadGameMessage(ServerMessageType type, ChessGame gameData, String playerType) {
        super(type);
        this.game = gameData;
        this.playerType = playerType;
    }

    public ChessGame getGameData() {
        return game;
    }

    public String getPlayerType() {
        return playerType;
    }
}
