package client;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.exceptions.NotFoundException;
import dataaccess.exceptions.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.requests.*;
import service.responses.GameListFormat;
import service.responses.JoinClearLogoutResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    RegisterRequest registerRequest = new RegisterRequest("mkeyes","mkeyes","mk");
    LoginRequest loginRequest = new LoginRequest("mkeyes","mkeyes");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String url = "http://localhost:"+port;
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear(){
        assertDoesNotThrow(()->facade.clear());
    }


    @Test
    public void registerValid() throws Exception{
        var authToken = facade.register(registerRequest);
        assertTrue(authToken.length()>10);
        RegisterRequest registerRequest2 = new RegisterRequest("m","keyes","mk");
        var authToken2 = facade.register(registerRequest2);
        assertTrue(authToken2.length()>10);
        assertNotEquals(authToken2,authToken);
    }

    @Test
    public void registerUsernameTakenThrowsException() throws Exception{
        facade.register(registerRequest);
        RegisterRequest registerRequest2 = new RegisterRequest("mkeyes","mkeyes","mk");
        assertThrows(AlreadyTakenException.class, ()->facade.register(registerRequest2));
    }

    @Test
    public void loginValid()throws Exception{
        facade.register(registerRequest);
        String authToken = facade.login(loginRequest);
        assertTrue(authToken.length()>10);
    }

    @Test
    public void loginThrowsException(){
        assertThrows(ResponseException.class,()->facade.login(loginRequest));
    }

    @Test
    public void logoutValid() throws Exception{
        facade.register(registerRequest);
        facade.login(loginRequest);
        String authToken = facade.logout();
        assertNull(authToken);
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken,"GameName");
        assertThrows(NotAuthorizedException.class,()->facade.createGame(createGameRequest));
    }

    @Test
    public void clearSuccess() throws Exception {
        facade.register(registerRequest);
        String authToken = facade.login(loginRequest);
        facade.clear();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken,"GameName");
        assertThrows(ResponseException.class,()->facade.createGame(createGameRequest));
    }

    @Test
    public void createGameValid() throws Exception{
        facade.register(registerRequest);
        String authToken = facade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken,"GameName");
        int gameId = facade.createGame(createGameRequest);
        assertTrue(gameId>0);
        CreateGameRequest createGameRequest2 = new CreateGameRequest(authToken,"GameName2");
        int gameId2 = facade.createGame(createGameRequest2);
        assertNotEquals(gameId2,gameId);
    }

    @Test
    public void createGameAlreadyExistsThrowsException()throws Exception{
        facade.register(registerRequest);
        String authToken = facade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken,"GameName");
        facade.createGame(createGameRequest);
        CreateGameRequest createGameRequest2 = new CreateGameRequest(authToken,"GameName");
        assertThrows(AlreadyTakenException.class,()->facade.createGame(createGameRequest2));
    }

    @Test
    public void listGamesValid() throws Exception{
        facade.register(registerRequest);
        String authToken = facade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken,"GameName");
        facade.createGame(createGameRequest);
        CreateGameRequest createGameRequest2 = new CreateGameRequest(authToken,"GameName2");
        facade.createGame(createGameRequest2);
        List<GameListFormat> gamesList = facade.listGames();
        assertEquals(2, gamesList.size());
    }

    @Test
    public void listGamesNotAuthorized(){
        assertThrows(ResponseException.class,()->facade.listGames());
    }

    @Test
    public void joinGameValid() throws Exception{
        facade.register(registerRequest);
        String authToken = facade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken,"GameName");
        int gameID = facade.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken,"WHITE",gameID);
        JoinClearLogoutResponse response = facade.joinGame(joinGameRequest);
        assertEquals("{}",response.message());
    }

    @Test
    public void joinGameNotFoundThrowsException() throws Exception{
        facade.register(registerRequest);
        String authToken = facade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken,"GameName");
        facade.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken,"WHITE",2);
        assertThrows(NotFoundException.class,()->facade.joinGame(joinGameRequest));
    }

}
