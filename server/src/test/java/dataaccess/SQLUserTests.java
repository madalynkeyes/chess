package dataaccess;

import dataaccess.exceptions.*;

import org.junit.jupiter.api.BeforeEach;

import service.shared.UserTests;



public class SQLUserTests extends UserTests {
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
}