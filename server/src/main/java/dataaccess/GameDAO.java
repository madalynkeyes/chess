package dataaccess;

import model.GameData;
import service.Responses.GameListFormat;

import java.util.List;

public interface GameDAO {
    void createGame(GameData gameData);
    GameData getGame(String gameID);
    void clear();

    List<GameListFormat> listGames();
}