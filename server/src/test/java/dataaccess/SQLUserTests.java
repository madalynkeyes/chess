package dataaccess;

import dataaccess.exceptions.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.UserService;
import service.requests.LoginRequest;
import service.requests.LogoutOrListGamesRequest;
import service.requests.RegisterRequest;
import service.responses.JoinClearLogoutResponse;
import service.responses.RegisterLoginResponse;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserTests extends SQLTests{
    private SQLAuthDAO authDAO;
    private UserService userService;
    @BeforeEach
    public void setup() throws ResponseException, DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void registerValidSuccess() throws ResponseException {
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        RegisterLoginResponse response = userService.register(request);
        assertEquals("mkeyes", response.username());
        String expected = "{\"username\":\"mkeyes\",\"authToken\":\"" + response.authToken() + "\"}";
        String actualJson = Serializer.toJson(response);
        assertEquals(expected, actualJson);
    }

    @Test
    public void registerUsernameTakenThrowsException() throws AlreadyTakenException, ResponseException {
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);
        RegisterRequest request2 = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        assertThrows(AlreadyTakenException.class, () -> userService.register(request2));
    }

    @Test
    public void registerNoUsernameThrowsException() throws BadRequestException {
        RegisterRequest request = new RegisterRequest(null, "123", "m@gmail.com");
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    public void registerNoPasswordThrowsException() throws BadRequestException {
        RegisterRequest request = new RegisterRequest("mk", null, "m@gmail.com");
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    public void registerNoEmailThrowsException() throws BadRequestException {
        RegisterRequest request = new RegisterRequest("mk", "123", null);
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    public void loginValidSuccess() throws NotAuthorizedException, ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(registerRequest);
        LoginRequest request = new LoginRequest("mkeyes", "123");
        RegisterLoginResponse response = userService.login(request);
        assertEquals("mkeyes", response.username());
        String expected = "{\"username\":\"mkeyes\",\"authToken\":\"" + response.authToken() + "\"}";
        String actualJson = Serializer.toJson(response);
        assertEquals(expected, actualJson);
    }

    @Test
    public void loginNotAuthorizedThrowsException() throws NotAuthorizedException, ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(registerRequest);
        LoginRequest request = new LoginRequest("mke", "123");
        assertThrows(NotAuthorizedException.class, () -> userService.login(request));
    }

    @Test
    public void loginNoUsernameThrowsException() throws BadRequestException, ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(registerRequest);
        LoginRequest request = new LoginRequest(null, "123");
        assertThrows(BadRequestException.class, () -> userService.login(request));
    }

    @Test
    public void loginNoPasswordThrowsException() throws BadRequestException, ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(registerRequest);
        LoginRequest request = new LoginRequest("mkeyes", null);
        assertThrows(BadRequestException.class, () -> userService.login(request));
    }

    @Test
    public void loginWrongPasswordThrowsException() throws NotAuthorizedException, ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(registerRequest);
        LoginRequest request = new LoginRequest("mkeyes", "123!");
        assertThrows(NotAuthorizedException.class, () -> userService.login(request));
    }

    @Test
    public void logoutValidSuccess() throws ResponseException {
        RegisterLoginResponse response = setUpLogout();
        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest(response.authToken());
        JoinClearLogoutResponse logoutResponse = userService.logout(request);
        assertEquals("{}", logoutResponse.message());
        assertNull(authDAO.getAuth(response.authToken()));
    }

    @NotNull
    private RegisterLoginResponse setUpLogout() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");
        userService.login(loginRequest);
        RegisterLoginResponse response = userService.login(loginRequest);
        assertNotNull(authDAO.getAuth(response.authToken()));
        return response;
    }

    @Test
    public void logoutNoAuthToken() throws BadRequestException, ResponseException {
        setUpLogout();
        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest(null);
        assertThrows(BadRequestException.class, () -> userService.logout(request));
    }

    @Test
    public void logoutWrongAuthToken() throws NotAuthorizedException, ResponseException {
        setUpLogout();
        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest("abc");
        assertThrows(NotAuthorizedException.class, () -> userService.logout(request));
    }

}
