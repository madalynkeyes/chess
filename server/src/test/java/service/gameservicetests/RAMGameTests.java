package service.gameservicetests;

import dataaccess.*;
import service.shared.GameTests;

public class RAMGameTests extends GameTests {
    @Override
    protected UserDAO createUserDAO() {
        return new RAMUserDAO();
    }

    @Override
    protected AuthDAO createAuthDAO() {
        return new RAMAuthDAO();
    }

    @Override
    protected GameDAO createGameDAO() {
        return new RAMGameDAO();
    }
}
