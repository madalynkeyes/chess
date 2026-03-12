package dataaccess;

import dataaccess.exceptions.DataAccessException;

import dataaccess.exceptions.ResponseException;
import org.junit.jupiter.api.BeforeEach;

import service.shared.ClearTests;



public class SQLClearTests extends ClearTests {
    @BeforeEach
    public void baseSetup(){
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