package dataaccess;

import com.google.gson.Gson;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ResponseException;
import model.GameData;
import service.responses.GameListFormat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO implements GameDAO{
    public SQLGameDAO() throws ResponseException, DataAccessException {
        SQLAuthDAO.configureDB(createStatements);
    }

    @Override
    public void createGame(GameData gameData) {
        var statement = "INSERT INTO games (gameId, gameName, whiteUsername, blackUsername, json) VALUES (?, ?, ?, ?, ?)";
        String json = new Gson().toJson(gameData);
        try {
            executeUpdate(statement, gameData.gameId(), gameData.gameName(), gameData.whiteUsername(), gameData.blackUsername(), json);
        } catch (ResponseException | DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateGame(GameData gameData, String username, String color) {
        String statement;
        if (color.equals("WHITE")) {
            statement = "UPDATE games SET whiteUsername=? WHERE gameID=? AND whiteUsername IS NULL";
        } else {
            statement = "UPDATE games SET blackUsername=? WHERE gameID=? AND blackUsername IS NULL";
        }
        try {
            executeUpdate(statement, username, gameData.gameId());
        } catch (ResponseException | DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGameByID(int gameID){
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            try {
                throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
            } catch (ResponseException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }

    @Override
    public GameData getGameByName(String gameName) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameName=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            try {
                throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
            } catch (ResponseException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }

    @Override
    public void clear() {
        var statement = "TRUNCATE games";
        try {
            executeClear(statement);
        } catch (ResponseException | DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GameListFormat> listGames() {
        List<GameListFormat> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameId, whiteUsername, blackUsername, gameName FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add( new GameListFormat(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            try {
                throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
            } catch (ResponseException ex) {
                throw new RuntimeException(ex);
            }
        }
        return result;
    }
    private GameData readGame(ResultSet rs) throws SQLException{
        var gameID = rs.getInt("gameID");
        var json = rs.getString("json");
        GameData gameData = new Gson().fromJson(json, GameData.class);
        return gameData.setId(gameID);
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException, DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                SQLAuthDAO.readParams(ps, params);
                if(ps.executeUpdate()==0){
                    throw new AlreadyTakenException("Error: color already taken");
                }
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }
            } catch (SQLException e) {
                throw new ResponseException(ResponseException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
            }
        }
    }

    private void executeClear(String statement, Object... params) throws ResponseException, DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                SQLAuthDAO.readParams(ps, params);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }
            } catch (SQLException e) {
                throw new ResponseException(ResponseException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
            }
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `gameName` varchar(255) NOT NULL,
              `whiteUsername` varchar(255),
              `blackUsername` varchar(255),
              `json` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
    };
}
