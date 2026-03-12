package service.userservicetests;

import dataaccess.AuthDAO;
import dataaccess.RAMAuthDAO;
import dataaccess.RAMUserDAO;
import dataaccess.UserDAO;
import service.shared.UserTests;

public class RAMUserTests extends UserTests {
    @Override
    protected UserDAO createUserDAO() {
        return new RAMUserDAO();
    }

    @Override
    protected AuthDAO createAuthDAO() {
        return new RAMAuthDAO();
    }
}
