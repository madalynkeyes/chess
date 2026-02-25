package service.GameServiceTests;

import dataaccess.*;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.GameService;
import service.Requests.CreateGameRequest;
import service.Requests.LoginRequest;
import service.Requests.RegisterRequest;
import service.Responses.CreateGameResponse;
import service.Responses.ListGamesResponse;
import service.Responses.RegisterResponse;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesTests {
    UserService userService;
    GameService gameService;
    RegisterResponse response;
    @BeforeEach
    public void initialize(){
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        GameDAO gameDAO = new RAMGameDAO();
        userService = new UserService(userDAO,authDAO,gameDAO);
        gameService = new GameService(userDAO,authDAO,gameDAO);
        RegisterRequest request = new RegisterRequest("mkeyes","123","m@gmail.com");
        userService.register(request);
        LoginRequest loginRequest = new LoginRequest("mkeyes","123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);
    }

    @Test
    public  void listGame_valid_success() {
        CreateGameRequest request = new CreateGameRequest(response.authToken(),"gameName");
        CreateGameResponse response1 = gameService.createGame(request);
        CreateGameRequest request1 = new CreateGameRequest(response.authToken(),"gameName1");
        CreateGameResponse response2 = gameService.createGame(request1);
        ListGamesResponse listGamesResponse= gameService.listGames(response.authToken());
        String expected = "{\"games\":[{\"gameID\":" + response2.gameID() +
                ",\"whiteUsername\":\"\",\"blackUsername\":\"\",\"gameName\":\"gameName1\"}," +
                "{\"gameID\":"+response1.gameID()+",\"whiteUsername\":\"\",\"blackUsername\":\"\",\"gameName\":\"gameName\"}]}";
        String actualJson = Serializer.toJson(listGamesResponse);
        assertEquals(expected,actualJson);
    }

    @Test
    public void listGame_empty_success(){
        ListGamesResponse listGamesResponse = gameService.listGames(response.authToken());
        assertTrue(listGamesResponse.games().isEmpty());
    }

    @Test
    public void listGame_noAuthToken_throwsException() throws BadRequestException{
        assertThrows(BadRequestException.class,() -> gameService.listGames(null));
    }

    @Test
    public void listGame_wrong_authToken_throwsException() throws NotAuthorizedException{
        assertThrows(NotAuthorizedException.class,() -> gameService.listGames("abc"));
    }
}
