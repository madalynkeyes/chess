package service.gameservicetests;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import service.GameService;
import service.UserService;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.responses.RegisterLoginResponse;


public abstract class GameServiceTests {
    protected UserDAO userDAO;
    protected AuthDAO authDAO;
    protected GameDAO gameDAO;
    protected UserService userService;
    protected GameService gameService;
    protected RegisterLoginResponse response;

    @BeforeEach
    public void baseSetup() {
        userDAO = new RAMUserDAO();
        authDAO = new RAMAuthDAO();
        gameDAO = new RAMGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);
        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);
    }
}
