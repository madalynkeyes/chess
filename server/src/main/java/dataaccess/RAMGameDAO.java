package dataaccess;

import model.GameData;
import service.responses.GameListFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RAMGameDAO implements GameDAO {
    private final Map<String, GameData> gamesByName = new HashMap<>();
    private final Map<Integer, GameData> gamesByID = new HashMap<>();

    @Override
    public GameData createGame(GameData gameData) {

        gamesByName.put(gameData.gameName(), gameData);
        gamesByID.put((gameData.gameId()), gameData);
        return gameData;
    }

    @Override
    public GameData getGameByID(int gameID) {
        return gamesByID.get(gameID);
    }

    @Override
    public GameData getGameByName(String gameName) {
        return gamesByName.get(gameName);
    }

    @Override
    public void clear() {
        gamesByName.clear();
    }

    @Override
    public List<GameListFormat> listGames() {
        List<GameListFormat> gameInfoList = new ArrayList<>();
        for (GameData gameData : gamesByName.values()) {
            gameInfoList.add(new GameListFormat(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName()));
        }
        return gameInfoList;
    }
}
