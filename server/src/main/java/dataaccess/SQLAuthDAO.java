package dataaccess;

import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ResponseException;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws ResponseException, DataAccessException {
        configureAuthDB();
    }

    @Override
    public void createAuth(AuthData authData) {
        var statement = "INSERT INTO auth (username, authToken, json) VALUES (?, ?, ?)";
        String json = new Gson().toJson(authData);
        try {
            executeUpdate(statement, authData.username(), authData.authToken(), json);
        } catch (DataAccessException | SQLException | ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(
                                rs.getString("username"),
                                rs.getString("authToken")
                        );
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
    public String getUserByToken(String authToken) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                       return rs.getString("username");
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
        var statement = "TRUNCATE auth";
        try {
            executeUpdate(statement);
        } catch (ResponseException | DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM auth WHERE authToken=?";
        try {
            executeUpdate(statement,authToken);
        } catch (ResponseException | DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException, DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                readParams(ps, params);
//                ps.executeUpdate();
                if(ps.executeUpdate()==1){
                    System.out.println("Added auth!");
                } else {System.out.println("Failed to add auth :(");
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        rs.getInt(1);
                    }

                }
            } catch (SQLException e) {
                throw new ResponseException(ResponseException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
            }
        }}

    static void readParams(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            switch (param) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case GameData p -> ps.setString(i + 1, p.toString());
                case null -> ps.setNull(i + 1, NULL);
                default -> {
                }
            }
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`)
            )
            """
    };

    private void configureAuthDB() throws ResponseException, DataAccessException {
        configureDB(createStatements);
    }

    static void configureDB(String[] createStatements) throws DataAccessException, ResponseException {
        DatabaseManager.createDatabase();
        try(Connection conn = DatabaseManager.getConnection()){
            for(String stmt : createStatements) {
                try(var preparedStatement = conn.prepareStatement(stmt)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e){
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure user database: %s",e.getMessage()));
        }
    }

}
