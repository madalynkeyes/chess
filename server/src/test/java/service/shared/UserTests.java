package service.shared;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.exceptions.ResponseException;
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

public abstract class UserTests {
    protected UserService userService;
    protected AuthDAO authDAO;

    protected abstract UserDAO createUserDAO() throws Exception;
    protected abstract AuthDAO createAuthDAO() throws Exception;

    @BeforeEach
    public void setup() throws Exception {
        UserDAO userDAO = createUserDAO();
        authDAO = createAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    // -------- REGISTER TESTS --------

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
    public void registerUsernameTakenThrowsException() throws Exception {
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);

        RegisterRequest request2 = new RegisterRequest("mkeyes", "123", "m@gmail.com");

        assertThrows(AlreadyTakenException.class, () -> userService.register(request2));
    }

    @Test
    public void registerNoUsernameThrowsException() {
        RegisterRequest request = new RegisterRequest(null, "123", "m@gmail.com");
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    public void registerNoPasswordThrowsException() {
        RegisterRequest request = new RegisterRequest("mk", null, "m@gmail.com");
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    public void registerNoEmailThrowsException() {
        RegisterRequest request = new RegisterRequest("mk", "123", null);
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    // -------- LOGIN TESTS --------

    @Test
    public void loginValidSuccess() throws Exception {
        userService.register(new RegisterRequest("mkeyes", "123", "m@gmail.com"));

        LoginRequest request = new LoginRequest("mkeyes", "123");

        RegisterLoginResponse response = userService.login(request);

        assertEquals("mkeyes", response.username());
    }

    @Test
    public void loginWrongPasswordThrowsException() throws Exception {
        userService.register(new RegisterRequest("mkeyes", "123", "m@gmail.com"));

        LoginRequest request = new LoginRequest("mkeyes", "123!");

        assertThrows(NotAuthorizedException.class, () -> userService.login(request));
    }

    // -------- LOGOUT TESTS --------

    private RegisterLoginResponse setUpLogout() throws Exception {
        userService.register(new RegisterRequest("mkeyes", "123", "m@gmail.com"));

        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");

        userService.login(loginRequest);

        RegisterLoginResponse response = userService.login(loginRequest);

        assertNotNull(authDAO.getAuth(response.authToken()));

        return response;
    }

    @Test
    public void logoutValidSuccess() throws Exception {
        RegisterLoginResponse response = setUpLogout();

        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest(response.authToken());

        JoinClearLogoutResponse logoutResponse = userService.logout(request);

        assertEquals("{}", logoutResponse.message());
        assertNull(authDAO.getAuth(response.authToken()));
    }

    @Test
    public void logoutWrongAuthToken() throws Exception {
        setUpLogout();

        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest("abc");

        assertThrows(NotAuthorizedException.class, () -> userService.logout(request));
    }
}
