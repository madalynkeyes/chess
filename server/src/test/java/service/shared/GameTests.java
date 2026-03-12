package service.shared;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.exceptions.NotFoundException;
import dataaccess.exceptions.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.GameService;
import service.UserService;
import service.requests.*;
import service.responses.CreateGameResponse;
import service.responses.ListGamesResponse;
import service.responses.RegisterLoginResponse;

import static org.junit.jupiter.api.Assertions.*;

public abstract class GameTests {
    protected UserDAO userDAO;
    protected AuthDAO authDAO;
    protected GameDAO gameDAO;

    protected UserService userService;
    protected GameService gameService;

    protected RegisterLoginResponse response;

    protected abstract UserDAO createUserDAO() throws Exception;
    protected abstract AuthDAO createAuthDAO() throws Exception;
    protected abstract GameDAO createGameDAO() throws Exception;

    @BeforeEach
    public void baseSetup() throws Exception {
        userDAO = createUserDAO();
        authDAO = createAuthDAO();
        gameDAO = createGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);

        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);

        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);
    }

    // ---------------- CREATE GAME ----------------

    @Test
    public void createGameValidSuccess() throws ResponseException {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), "gameName");

        CreateGameResponse createGameResponse = gameService.createGame(request);

        assertTrue(createGameResponse.gameID() >= 0);
    }

    @Test
    public void createMultipleGamesSuccess() throws ResponseException {
        CreateGameResponse g1 = gameService.createGame(
                new CreateGameRequest(response.authToken(), "gameName"));

        CreateGameResponse g2 = gameService.createGame(
                new CreateGameRequest(response.authToken(), "gameName1"));

        assertTrue(g1.gameID() >= 0);
        assertTrue(g2.gameID() >= 0);
    }

    @Test
    public void createGameNoAuthTokenThrowsException() {
        CreateGameRequest request = new CreateGameRequest(null, "gameName");

        assertThrows(BadRequestException.class,
                () -> gameService.createGame(request));
    }

    @Test
    public void createGameWrongAuthTokenThrowsException() {
        CreateGameRequest request = new CreateGameRequest("abc", "gameName");

        assertThrows(NotAuthorizedException.class,
                () -> gameService.createGame(request));
    }

    @Test
    public void createGameNoGameNameThrowsException() {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), null);

        assertThrows(BadRequestException.class,
                () -> gameService.createGame(request));
    }

    @Test
    public void createGameGameNameAlreadyTakenThrowsException() throws Exception {
        gameService.createGame(new CreateGameRequest(response.authToken(), "gameName"));

        assertThrows(AlreadyTakenException.class,
                () -> gameService.createGame(
                        new CreateGameRequest(response.authToken(), "gameName")));
    }

    // ---------------- JOIN GAME ----------------

    private CreateGameResponse createGame() throws ResponseException {
        return gameService.createGame(
                new CreateGameRequest(response.authToken(), "gameName"));
    }

    @Test
    public void joinGameWhiteValidSuccess() throws Exception {
        CreateGameResponse game = createGame();

        gameService.joinGame(
                new JoinGameRequest(response.authToken(), "WHITE", game.gameID()));

        ListGamesResponse list = gameService.listGames(
                new LogoutOrListGamesRequest(response.authToken()));

        String expected = "{\"games\":[{\"gameID\":" + game.gameID() +
                ",\"whiteUsername\":\"mkeyes\",\"gameName\":\"gameName\"}]}";

        assertEquals(expected, Serializer.toJson(list));
    }

    @Test
    public void joinGameBlackValidSuccess() throws Exception {
        CreateGameResponse game = createGame();

        gameService.joinGame(
                new JoinGameRequest(response.authToken(), "BLACK", game.gameID()));

        ListGamesResponse list = gameService.listGames(
                new LogoutOrListGamesRequest(response.authToken()));

        String expected = "{\"games\":[{\"gameID\":" + game.gameID() +
                ",\"blackUsername\":\"mkeyes\",\"gameName\":\"gameName\"}]}";

        assertEquals(expected, Serializer.toJson(list));
    }

    @Test
    public void joinGameNotAuthorizedThrowsException() throws Exception {
        CreateGameResponse game = createGame();

        assertThrows(NotAuthorizedException.class,
                () -> gameService.joinGame(
                        new JoinGameRequest("abc", "WHITE", game.gameID())));
    }

    @Test
    public void joinGameGameNotFoundThrowsException() {
        assertThrows(NotFoundException.class,
                () -> gameService.joinGame(
                        new JoinGameRequest(response.authToken(), "WHITE", 1234)));
    }

    @Test
    public void joinGameColorAlreadyTakenThrowsException() throws Exception {
        CreateGameResponse game = createGame();

        gameService.joinGame(
                new JoinGameRequest(response.authToken(), "WHITE", game.gameID()));

        assertThrows(AlreadyTakenException.class,
                () -> gameService.joinGame(
                        new JoinGameRequest(response.authToken(), "WHITE", game.gameID())));
    }

    // ---------------- LIST GAMES ----------------

    @Test
    public void listGameValidSuccess() throws Exception {
        CreateGameResponse g1 =
                gameService.createGame(new CreateGameRequest(response.authToken(), "gameName"));

        CreateGameResponse g2 =
                gameService.createGame(new CreateGameRequest(response.authToken(), "gameName1"));

        ListGamesResponse list =
                gameService.listGames(new LogoutOrListGamesRequest(response.authToken()));

        String expected = "{\"games\":[{\"gameID\":" + g2.gameID() +
                ",\"gameName\":\"gameName1\"}," +
                "{\"gameID\":" + g1.gameID() +
                ",\"gameName\":\"gameName\"}]}";

        assertEquals(expected, Serializer.toJson(list));
    }

    @Test
    public void listGameEmptySuccess() throws ResponseException {
        ListGamesResponse list =
                gameService.listGames(new LogoutOrListGamesRequest(response.authToken()));

        assertTrue(list.games().isEmpty());
    }

    @Test
    public void listGameNoAuthTokenThrowsException() {
        assertThrows(BadRequestException.class,
                () -> gameService.listGames(
                        new LogoutOrListGamesRequest(null)));
    }

    @Test
    public void listGameWrongAuthTokenThrowsException() {
        assertThrows(NotAuthorizedException.class,
                () -> gameService.listGames(
                        new LogoutOrListGamesRequest("abc")));
    }
}
