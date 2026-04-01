package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage{
    private final String playerType;
    private final GameData gameData;
    public LoadGameMessage(ServerMessageType type, GameData gameData, String playerType) {
        super(type);
        this.gameData = gameData;
        this.playerType = playerType;
    }

    public GameData getGameData() {
        return gameData;
    }

    public String getPlayerType() {
        return playerType;
    }
}
