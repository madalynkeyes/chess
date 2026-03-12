package service.shared;

import dataaccess.*;
import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.exceptions.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import service.requests.*;
import service.responses.ListGamesResponse;
import service.responses.RegisterLoginResponse;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ClearTests {

    protected UserDAO userDAO;
    protected AuthDAO authDAO;
    protected GameDAO gameDAO;

    protected UserService userService;
    protected GameService gameService;
    protected ClearService clearService;

    protected RegisterLoginResponse response;

    protected abstract UserDAO createUserDAO() throws Exception;
    protected abstract AuthDAO createAuthDAO() throws Exception;
    protected abstract GameDAO createGameDAO() throws Exception;

    @BeforeEach
    public void initialize() throws Exception {

        userDAO = createUserDAO();
        authDAO = createAuthDAO();
        gameDAO = createGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);

        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);

        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);

        CreateGameRequest createGameRequest =
                new CreateGameRequest(response.authToken(), "gameName");

        gameService.createGame(createGameRequest);

        LogoutOrListGamesRequest listGamesRequest =
                new LogoutOrListGamesRequest(response.authToken());

        ListGamesResponse listGamesResponse =
                gameService.listGames(listGamesRequest);

        assertFalse(listGamesResponse.games().isEmpty());
    }

    @Test
    public void clearSuccessNoLongerAuthorized() throws ResponseException {

        clearService.clear();

        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");

        assertThrows(NotAuthorizedException.class,
                () -> userService.login(loginRequest));

        CreateGameRequest createGameRequest =
                new CreateGameRequest(response.authToken(), "gameName");

        assertThrows(NotAuthorizedException.class,
                () -> gameService.createGame(createGameRequest));

        LogoutOrListGamesRequest listGamesRequest =
                new LogoutOrListGamesRequest(response.authToken());

        assertThrows(NotAuthorizedException.class,
                () -> gameService.listGames(listGamesRequest));
    }

    @Test
    public void clearSuccessGamesListEmpty() throws ResponseException {

        clearService.clear();

        RegisterRequest registerRequest =
                new RegisterRequest("mkeyes", "123", "m@gmail.com");

        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");

        userService.login(loginRequest);
        response = userService.login(loginRequest);

        LogoutOrListGamesRequest listGamesRequest =
                new LogoutOrListGamesRequest(response.authToken());

        ListGamesResponse listGamesResponse =
                gameService.listGames(listGamesRequest);

        assertTrue(listGamesResponse.games().isEmpty());
    }
}
