package dataaccess;

import model.GameData;
import service.Responses.GameListFormat;

import java.util.List;

public interface GameDAO {
    void createGame(GameData gameData);

    GameData getGameByID(int gameID);

    GameData getGameByName(String gameName);

    void clear();

    List<GameListFormat> listGames();
}