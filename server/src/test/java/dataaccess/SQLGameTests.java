package dataaccess;

import org.junit.jupiter.api.BeforeEach;

import service.shared.GameTests;

public class SQLGameTests extends GameTests {

    @BeforeEach
    public void setup(){
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
