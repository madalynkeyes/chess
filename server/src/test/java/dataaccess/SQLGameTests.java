package dataaccess;

import dataaccess.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.GameService;
import service.UserService;
import service.requests.*;
import service.responses.CreateGameResponse;
import service.responses.JoinClearLogoutResponse;
import service.responses.ListGamesResponse;
import service.responses.RegisterLoginResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SQLGameTests extends SQLTests{
    private GameService gameService;
    private RegisterLoginResponse response;
    @BeforeEach
    public void setup() throws ResponseException, DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        SQLGameDAO gameDAO = new SQLGameDAO();
        UserService userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);
        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);
    }

    @Test
    public void createGameValidSuccess() throws ResponseException {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), "gameName");
        CreateGameResponse createGameResponse = gameService.createGame(request);
        int idValue = createGameResponse.gameID();
        assertTrue(idValue >= 0);
    }

    @Test
    public void createMultipleGamesSuccess() throws ResponseException {
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
    public void createGameNoAuthTokenThrowsException() throws BadRequestException {
        CreateGameRequest request = new CreateGameRequest(null, "gameName");
        assertThrows(BadRequestException.class, () -> gameService.createGame(request));
    }

    @Test
    public void createGameWrongAuthTokenThrowsException() throws NotAuthorizedException {
        CreateGameRequest request = new CreateGameRequest("abc", "gameName");
        assertThrows(NotAuthorizedException.class, () -> gameService.createGame(request));
    }

    @Test
    public void createGameNoGameNameThrowsException() throws BadRequestException {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), null);
        assertThrows(BadRequestException.class, () -> gameService.createGame(request));
    }

    @Test
    public void createGameGameNameAlreadyTakenThrowsException() throws AlreadyTakenException, ResponseException {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), "gameName");
        gameService.createGame(request);
        CreateGameRequest request2 = new CreateGameRequest(response.authToken(), "gameName");
        assertThrows(AlreadyTakenException.class, () -> gameService.createGame(request2));
    }

    @Test
    public void joinGameWhiteValidSuccess() throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest(response.authToken(), "gameName");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
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
    public void joinGameBlackValidSuccess() throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest(response.authToken(), "gameName");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
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
    public void joinGameNotAuthorizedThrowsException() throws NotAuthorizedException, ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest(response.authToken(), "gameName");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        JoinGameRequest request = new JoinGameRequest("abc", "WHITE", createGameResponse.gameID());
        assertThrows(NotAuthorizedException.class, () -> gameService.joinGame(request));
    }

    @Test
    public void joinGameGameNotFoundThrowsException() throws NotFoundException, ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest(response.authToken(), "gameName");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        JoinGameRequest request = new JoinGameRequest(response.authToken(), "WHITE", 1234);
        assertThrows(NotFoundException.class, () -> gameService.joinGame(request));
    }

    @Test
    public void joinGameColorAlreadyTakenThrowsException() throws AlreadyTakenException, ResponseException {
        CreateGameResponse createGameResponse = joinGameSetUp();
        JoinGameRequest request = new JoinGameRequest(response.authToken(), "WHITE", createGameResponse.gameID());
        gameService.joinGame(request);
        JoinGameRequest request2 = new JoinGameRequest(response.authToken(), "WHITE", createGameResponse.gameID());
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(request2));
    }

    private CreateGameResponse joinGameSetUp() throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest(response.authToken(), "gameName");
        return gameService.createGame(createGameRequest);
    }

    @Test
    public void listGameValidSuccess() throws ResponseException {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), "gameName");
        CreateGameResponse response1 = gameService.createGame(request);
        CreateGameRequest request1 = new CreateGameRequest(response.authToken(), "gameName1");
        CreateGameResponse response2 = gameService.createGame(request1);
        LogoutOrListGamesRequest listGamesRequest = new LogoutOrListGamesRequest(response.authToken());
        ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);
        String expected = "{\"games\":[{\"gameID\":" + response1.gameID() +
                ",\"gameName\":\"gameName\"}," +
                "{\"gameID\":" + response2.gameID() + ",\"gameName\":\"gameName1\"}]}";
        String actualJson = Serializer.toJson(listGamesResponse);
        assertEquals(expected, actualJson);
    }

    @Test
    public void listGameEmptySuccess() throws ResponseException {
        LogoutOrListGamesRequest listGamesRequest = new LogoutOrListGamesRequest(response.authToken());
        ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);
        assertTrue(listGamesResponse.games().isEmpty());
    }

    @Test
    public void listGameNoAuthTokenThrowsException() throws BadRequestException {
        LogoutOrListGamesRequest listGamesRequest = new LogoutOrListGamesRequest(null);
        assertThrows(BadRequestException.class, () -> gameService.listGames(listGamesRequest));
    }

    @Test
    public void listGameWrongAuthTokenThrowsException() throws NotAuthorizedException {
        LogoutOrListGamesRequest listGamesRequest = new LogoutOrListGamesRequest("abc");
        assertThrows(NotAuthorizedException.class, () -> gameService.listGames(listGamesRequest));
    }

}
