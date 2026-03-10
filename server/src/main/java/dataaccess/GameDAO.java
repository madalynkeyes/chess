package dataaccess;

import model.GameData;
import service.responses.GameListFormat;

import java.util.List;

public interface GameDAO {
    GameData createGame(GameData gameData);

    GameData getGameByID(int gameID);

    GameData getGameByName(String gameName);

    void clear();

    List<GameListFormat> listGames();
}