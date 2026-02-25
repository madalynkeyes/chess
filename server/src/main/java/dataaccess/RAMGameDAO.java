package dataaccess;

import model.GameData;
import service.Responses.GameListFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RAMGameDAO implements GameDAO{
    private final Map<String, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData gameData) {
        games.put(gameData.gameName(),gameData);
    }

    @Override
    public GameData getGame(String gameID) {
        return games.get(gameID);
    }

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public List<GameListFormat> listGames() {
        List<GameListFormat>gameInfoList = new ArrayList<>();
        for (GameData gameData:games.values()){
            gameInfoList.add(new GameListFormat(gameData.gameId(),gameData.whiteUsername(),gameData.blackUsername(),gameData.gameName()));
        }
        return gameInfoList;
    }
}
