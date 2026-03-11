package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ResponseException;
import model.GameData;
import service.responses.GameListFormat;

import java.sql.SQLException;
import java.util.List;

public interface GameDAO {
    void createGame(GameData gameData) throws ResponseException;

    void updateGame(GameData gameData, String username, String color) throws ResponseException, SQLException, DataAccessException;

    GameData getGameByID(int gameID) throws ResponseException;

    GameData getGameByName(String gameName) throws ResponseException;

    void clear() throws ResponseException;

    List<GameListFormat> listGames() throws ResponseException;
}