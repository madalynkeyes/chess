package service.UserServiceTests;

import dataaccess.*;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;
import service.Requests.LoginRequest;
import service.Requests.LogoutOrListGamesRequest;
import service.Requests.RegisterRequest;
import service.Responses.JoinClearLogoutResponse;
import service.Responses.RegisterLoginResponse;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTests {
    UserService userService;
    RegisterLoginResponse response;
    AuthDAO authDAO = new RAMAuthDAO();

    @BeforeEach
    public void initialize() {
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
    public void logout_valid_success() {
        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest(response.authToken());
        JoinClearLogoutResponse logoutResponse = userService.logout(request);
        assertEquals("{}", logoutResponse.message());
        assertNull(authDAO.getAuth(response.authToken()));
    }

    @Test
    public void logout_no_authToken() throws BadRequestException {
        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest(null);
        assertThrows(BadRequestException.class, () -> userService.logout(request));
    }

    @Test
    public void logout_wrong_authToken() throws NotAuthorizedException {
        LogoutOrListGamesRequest request = new LogoutOrListGamesRequest("abc");
        assertThrows(NotAuthorizedException.class, () -> userService.logout(request));
    }
}
