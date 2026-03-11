package service.gameservicetests;

import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.exceptions.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import service.requests.CreateGameRequest;

import service.responses.CreateGameResponse;
import static org.junit.jupiter.api.Assertions.*;

public class CreateGameTests extends GameServiceTests {


    @BeforeEach
    public void setup() {
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
}
