package service.UserServiceTests;

import dataaccess.*;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.Requests.LoginRequest;
import service.Requests.RegisterRequest;
import service.Responses.RegisterLoginResponse;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests {
    UserService userService;

    @BeforeEach
    public void initialize() {
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        userService = new UserService(userDAO, authDAO);
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);
    }

    @Test
    public void login_valid_success() throws NotAuthorizedException {
        LoginRequest request = new LoginRequest("mkeyes", "123");
        RegisterLoginResponse response = userService.login(request);
        assertEquals("mkeyes", response.username());
        String expected = "{\"username\":\"mkeyes\",\"authToken\":\"" + response.authToken() + "\"}";
        String actualJson = Serializer.toJson(response);
        assertEquals(expected, actualJson);
    }

    @Test
    public void login_notAuthorized_throwsException() throws NotAuthorizedException {
        LoginRequest request = new LoginRequest("mke", "123");
        assertThrows(NotAuthorizedException.class, () -> userService.login(request));
    }

    @Test
    public void login_noUsername_throwsException() throws BadRequestException {
        LoginRequest request = new LoginRequest(null, "123");
        assertThrows(BadRequestException.class, () -> userService.login(request));
    }

    @Test
    public void login_noPassword_throwsException() throws BadRequestException {
        LoginRequest request = new LoginRequest("mkeyes", null);
        assertThrows(BadRequestException.class, () -> userService.login(request));
    }

    @Test
    public void login_wrongPassword_throwsException() throws NotAuthorizedException {
        LoginRequest request = new LoginRequest("mkeyes", "123!");
        assertThrows(NotAuthorizedException.class, () -> userService.login(request));
    }
}
