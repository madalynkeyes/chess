package dataaccess;

import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.ResponseException;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLUserDAO implements UserDAO{


    public SQLUserDAO() throws ResponseException, DataAccessException {
        configureUserDB();
    }

    @Override
    public void createUser(UserData userData) {
        var statement = "INSERT INTO users (username, password, email, json) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(userData);
        try {
            executeUpdate(statement, userData.username(), userData.password(), userData.email(), json);
        } catch (DataAccessException | SQLException | ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
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
    public void clear() {
        var statement = "TRUNCATE users";
        try {
            executeUpdate(statement);
        } catch (ResponseException | DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException, DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                SQLAuthDAO.readParams(ps, params);
//                ps.executeUpdate();
                if(ps.executeUpdate()==1){
                    System.out.println("Added user!");
                } else {System.out.println("Failed to add user :(");
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }

            }
        } catch (SQLException e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }}

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`)
            )
            """
    };

    private void configureUserDB() throws ResponseException, DataAccessException {
        SQLAuthDAO.configureDB(createStatements);
    }
}
