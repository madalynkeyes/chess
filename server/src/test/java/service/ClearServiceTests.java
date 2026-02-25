package service;

import dataaccess.*;
import dataaccess.exceptions.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.*;
import service.responses.ListGamesResponse;
import service.responses.RegisterLoginResponse;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    UserService userService;
    ClearService clearService;
    GameService gameService;
    RegisterLoginResponse response;

    @BeforeEach
    public void initialize() {
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        GameDAO gameDAO = new RAMGameDAO();
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
