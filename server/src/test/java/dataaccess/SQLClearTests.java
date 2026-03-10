package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.exceptions.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.requests.CreateGameRequest;
import service.requests.LoginRequest;
import service.requests.LogoutOrListGamesRequest;
import service.requests.RegisterRequest;
import service.responses.ListGamesResponse;
import service.responses.RegisterLoginResponse;

import static org.junit.jupiter.api.Assertions.*;

public class SQLClearTests extends SQLTests{
    private SQLUserDAO userDAO;
    private SQLAuthDAO authDAO;
    private SQLGameDAO gameDAO;
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;
    private RegisterLoginResponse response;
    @BeforeEach
    public void initialize() throws ResponseException, DataAccessException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);
        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(response.authToken(), "gameName");
        gameService.createGame(createGameRequest);
        LogoutOrListGamesRequest listGamesRequest = new LogoutOrListGamesRequest(response.authToken());
        ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);
        assertFalse(listGamesResponse.games().isEmpty());
    }

    @Test
    public void clearSuccessNoLongerAuthorized() {
        clearService.clear();
        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");
        assertThrows(NotAuthorizedException.class, () -> userService.login(loginRequest));
        CreateGameRequest createGameRequest = new CreateGameRequest(response.authToken(), "gameName");
        assertThrows(NotAuthorizedException.class, () -> gameService.createGame(createGameRequest));
        LogoutOrListGamesRequest listGamesRequest = new LogoutOrListGamesRequest(response.authToken());
        assertThrows(NotAuthorizedException.class, () -> gameService.listGames(listGamesRequest));
    }

    @Test
    public void clearSuccessGamesListEmpty() {
        clearService.clear();
        RegisterRequest registerRequest = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);
        LogoutOrListGamesRequest listGamesRequest = new LogoutOrListGamesRequest(response.authToken());
        ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);
        assertTrue(listGamesResponse.games().isEmpty());

    }
}
