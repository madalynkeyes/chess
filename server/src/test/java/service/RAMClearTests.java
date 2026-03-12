package service;

import dataaccess.*;
import service.shared.ClearTests;

public class RAMClearTests extends ClearTests {
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
