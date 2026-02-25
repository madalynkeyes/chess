package service.GameServiceTests;

import dataaccess.*;
import dataaccess.Exceptions.AlreadyTakenException;
import dataaccess.Exceptions.NotAuthorizedException;
import dataaccess.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.GameService;
import service.Requests.*;
import service.Responses.CreateGameResponse;
import service.Responses.JoinClearLogoutResponse;
import service.Responses.ListGamesResponse;
import service.Responses.RegisterLoginResponse;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameTests {
    UserService userService;
    GameService gameService;
    RegisterLoginResponse response;
    CreateGameResponse createGameResponse;

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
        CreateGameRequest createGameRequest = new CreateGameRequest(response.authToken(), "gameName");
        createGameResponse = gameService.createGame(createGameRequest);
    }

    @Test
    public void joinGame_white_valid_success() {
        JoinGameRequest request = new JoinGameRequest(response.authToken(), "WHITE", createGameResponse.gameID());
        JoinClearLogoutResponse joinGameResponse = gameService.joinGame(request);
        assertEquals("{}", joinGameResponse.message());
        LogoutOrListGamesRequest listGamesRequest = new LogoutOrListGamesRequest(response.authToken());
        ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);
        String expected = "{\"games\":[{\"gameID\":" + createGameResponse.gameID() +
                ",\"whiteUsername\":\"mkeyes\",\"gameName\":\"gameName\"}]}";
        String actualJson = Serializer.toJson(listGamesResponse);
        assertEquals(expected, actualJson);
    }

    @Test
    public void joinGame_black_valid_success() {
        JoinGameRequest request = new JoinGameRequest(response.authToken(), "BLACK", createGameResponse.gameID());
        JoinClearLogoutResponse joinGameResponse = gameService.joinGame(request);
        assertEquals("{}", joinGameResponse.message());
        LogoutOrListGamesRequest listGamesRequest = new LogoutOrListGamesRequest(response.authToken());
        ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);
        String expected = "{\"games\":[{\"gameID\":" + createGameResponse.gameID() +
                ",\"blackUsername\":\"mkeyes\",\"gameName\":\"gameName\"}]}";
        String actualJson = Serializer.toJson(listGamesResponse);
        assertEquals(expected, actualJson);
    }

    @Test
    public void joinGame_notAuthorized_throwsException() throws NotAuthorizedException {
        JoinGameRequest request = new JoinGameRequest("abc", "WHITE", createGameResponse.gameID());
        assertThrows(NotAuthorizedException.class, () -> gameService.joinGame(request));
    }

    @Test
    public void joinGame_gameNotFound_throwsException() throws NotFoundException {
        JoinGameRequest request = new JoinGameRequest(response.authToken(), "WHITE", 1234);
        assertThrows(NotFoundException.class, () -> gameService.joinGame(request));
    }

    @Test
    public void joinGame_colorAlreadyTaken_throwsException() throws AlreadyTakenException {
        JoinGameRequest request = new JoinGameRequest(response.authToken(), "WHITE", createGameResponse.gameID());
        gameService.joinGame(request);
        JoinGameRequest request2 = new JoinGameRequest(response.authToken(), "WHITE", createGameResponse.gameID());
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(request2));
    }


}
