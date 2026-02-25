package service.gameservicetests;

import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.requests.*;
import service.responses.CreateGameResponse;
import service.responses.JoinClearLogoutResponse;
import service.responses.ListGamesResponse;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameTests extends GameServiceTests {
    CreateGameResponse createGameResponse;

    @BeforeEach
    public void setup() {
        CreateGameRequest createGameRequest = new CreateGameRequest(response.authToken(), "gameName");
        createGameResponse = gameService.createGame(createGameRequest);
    }

    @Test
    public void joinGameWhiteValidSuccess() {
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
    public void joinGameBlackValidSuccess() {
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
    public void joinGameNotAuthorizedThrowsException() throws NotAuthorizedException {
        JoinGameRequest request = new JoinGameRequest("abc", "WHITE", createGameResponse.gameID());
        assertThrows(NotAuthorizedException.class, () -> gameService.joinGame(request));
    }

    @Test
    public void joinGameGameNotFoundThrowsException() throws NotFoundException {
        JoinGameRequest request = new JoinGameRequest(response.authToken(), "WHITE", 1234);
        assertThrows(NotFoundException.class, () -> gameService.joinGame(request));
    }

    @Test
    public void joinGameColorAlreadyTakenThrowsException() throws AlreadyTakenException {
        JoinGameRequest request = new JoinGameRequest(response.authToken(), "WHITE", createGameResponse.gameID());
        gameService.joinGame(request);
        JoinGameRequest request2 = new JoinGameRequest(response.authToken(), "WHITE", createGameResponse.gameID());
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(request2));
    }


}
