package dataaccess;

import dataaccess.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.GameService;
import service.UserService;
import service.requests.*;
import service.responses.CreateGameResponse;
import service.responses.JoinClearLogoutResponse;
import service.responses.ListGamesResponse;
import service.responses.RegisterLoginResponse;
import service.shared.GameTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SQLGameTests extends GameTests {

    @BeforeEach
    public void setup(){
        try {
            new SQLUserDAO().clear();
            new SQLGameDAO().clear();
            new SQLAuthDAO().clear();
        } catch (ResponseException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected UserDAO createUserDAO() throws Exception {
        return new SQLUserDAO();
    }

    @Override
    protected AuthDAO createAuthDAO() throws Exception {
        return new SQLAuthDAO();
    }

    @Override
    protected GameDAO createGameDAO() throws Exception {
        return new SQLGameDAO();
    }
}
