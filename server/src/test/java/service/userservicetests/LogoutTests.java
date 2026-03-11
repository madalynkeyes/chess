package service.userservicetests;

import dataaccess.*;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.exceptions.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;
import service.requests.LoginRequest;
import service.requests.LogoutOrListGamesRequest;
import service.requests.RegisterRequest;
import service.responses.JoinClearLogoutResponse;
import service.responses.RegisterLoginResponse;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTests {
    UserService userService;
    RegisterLoginResponse response;
    AuthDAO authDAO = new RAMAuthDAO();

    @BeforeEach
    public void initialize() throws ResponseException {
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        userService = new UserService(userDAO, authDAO);
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);
        LoginRequest loginRequest = new LoginRequest("mkeyes", "123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);
        assertNotNull(authDAO.getAuth(response.authToken()));
    }

    @Test
    public void logoutValidSuccess() throws ResponseException {
        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest(response.authToken());
        JoinClearLogoutResponse logoutResponse = userService.logout(request);
        assertEquals("{}", logoutResponse.message());
        assertNull(authDAO.getAuth(response.authToken()));
    }

    @Test
    public void logoutNoAuthToken() throws BadRequestException {
        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest(null);
        assertThrows(BadRequestException.class, () -> userService.logout(request));
    }

    @Test
    public void logoutWrongAuthToken() throws NotAuthorizedException {
        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest("abc");
        assertThrows(NotAuthorizedException.class, () -> userService.logout(request));
    }
}
