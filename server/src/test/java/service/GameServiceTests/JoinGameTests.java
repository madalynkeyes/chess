package service.GameServiceTests;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.Requests.CreateGameRequest;
import service.Requests.JoinGameRequest;
import service.Requests.LoginRequest;
import service.Requests.RegisterRequest;
import service.Responses.CreateGameResponse;
import service.Responses.JoinGameResponse;
import service.Responses.RegisterResponse;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameTests {
    UserService userService;
    GameService gameService;
    RegisterResponse response;
    CreateGameResponse createGameResponse;
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
        CreateGameRequest createGameRequest = new CreateGameRequest(response.authToken(),"gameName");
        createGameResponse = gameService.createGame(createGameRequest);
    }

    @Test
    public  void joinGame_valid_success() {
        JoinGameRequest request = new JoinGameRequest(response.authToken(),"WHITE",createGameResponse.gameID());
        JoinGameResponse joinGameResponse = gameService.joinGame(request);
        assertEquals("{}",joinGameResponse.message());
//        ListGamesResponse listGamesResponse = gameService.listGames(response.authToken());
//        System.out.println(listGamesResponse);
//        assertEquals("mkeyes",listGamesResponse.games());
    }


}
