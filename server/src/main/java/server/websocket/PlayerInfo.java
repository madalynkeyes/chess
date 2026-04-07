package server.websocket;

public class PlayerInfo {
    private final String username;
    private final int gameID;
    private final String playerType;

    public PlayerInfo(String username, int gameID, String playerType) {
        this.username = username;
        this.gameID = gameID;
        this.playerType = playerType;
    }

    public String getUsername() {
        return username;
    }

    public int getGameID() {
        return gameID;
    }

    public String getPlayerType() {
        return playerType;
    }
}


