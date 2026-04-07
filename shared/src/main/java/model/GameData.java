package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameId, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData setId(int gameId){
        return new GameData(gameId,this.whiteUsername,this.blackUsername,this.gameName, this.game);
    }

    public String toString(){
        return new Gson().toJson(this);
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }
}
