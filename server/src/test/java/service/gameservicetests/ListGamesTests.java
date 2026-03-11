package service.gameservicetests;

import dataaccess.*;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.exceptions.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.requests.CreateGameRequest;
import service.requests.LogoutOrListGamesRequest;
import service.responses.CreateGameResponse;
import service.responses.ListGamesResponse;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesTests extends GameServiceTests {


    @BeforeEach
    public void setup() {
    }

    @Test
    public void listGameValidSuccess() throws ResponseException {
        CreateGameRequest request = new CreateGameRequest(response.authToken(), "gameName");
        CreateGameResponse response1 = gameService.createGame(request);
        CreateGameRequest request1 = new CreateGameRequest(response.authToken(), "gameName1");
        CreateGameResponse response2 = gameService.createGame(request1);
        LogoutOrListGamesRequest listGamesRequest = new LogoutOrListGamesRequest(response.authToken());
        ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);
        String expected = "{\"games\":[{\"gameID\":" + response2.gameID() +
                ",\"gameName\":\"gameName1\"}," +
                "{\"gameID\":" + response1.gameID() + ",\"gameName\":\"gameName\"}]}";
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
