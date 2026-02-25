package service.GameServiceTests;

import dataaccess.*;
import dataaccess.Exceptions.AlreadyTakenException;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.Requests.CreateGameRequest;
import service.Requests.LoginRequest;
import service.Requests.RegisterRequest;
import service.Responses.CreateGameResponse;
import service.Responses.RegisterLoginResponse;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameTests {
    UserService userService;
    GameService gameService;
    RegisterLoginResponse response;

    @BeforeEach
    public void initialize() {
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        GameDAO gameDAO = new RAMGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);
        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);
    }

    @Test
    public void createGame_valid_success() {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), "gameName");
        CreateGameResponse createGameResponse = gameService.createGame(request);
        int idValue = createGameResponse.gameID();
        assertTrue(idValue >= 0);
    }

    @Test
    public void create_multiple_games_success() {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), "gameName");
        CreateGameResponse createGameResponse = gameService.createGame(request);
        int idValue = createGameResponse.gameID();
        assertTrue(idValue >= 0);
        CreateGameRequest request2 = new CreateGameRequest(response.authToken(), "gameName1");
        CreateGameResponse createGameResponse2 = gameService.createGame(request2);
        int idValue2 = createGameResponse2.gameID();
        assertTrue(idValue2 >= 0);
    }

    @Test
    public void createGame_no_authToken_throwsException() throws BadRequestException {
        CreateGameRequest request = new CreateGameRequest(null, "gameName");
        assertThrows(BadRequestException.class, () -> gameService.createGame(request));
    }

    @Test
    public void createGame_wrong_authToken_throwsException() throws NotAuthorizedException {
        CreateGameRequest request = new CreateGameRequest("abc", "gameName");
        assertThrows(NotAuthorizedException.class, () -> gameService.createGame(request));
    }

    @Test
    public void createGame_no_gameName_throwsException() throws BadRequestException {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), null);
        assertThrows(BadRequestException.class, () -> gameService.createGame(request));
    }

    @Test
    public void createGame_gameName_already_taken_throwsException() throws AlreadyTakenException {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), "gameName");
        gameService.createGame(request);
        CreateGameRequest request2 = new CreateGameRequest(response.authToken(), "gameName");
        assertThrows(AlreadyTakenException.class, () -> gameService.createGame(request2));
    }
}
