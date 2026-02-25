package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.responses.JoinClearLogoutResponse;

public class ClearService extends Service {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        super(authDAO);
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    /**
     * Clear Service Class
     * Clears auth data, game data and user data.
     *
     * @return clear response
     */
    public JoinClearLogoutResponse clear() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        return new JoinClearLogoutResponse("{}");
    }
}
