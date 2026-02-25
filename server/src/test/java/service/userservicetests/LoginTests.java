package service.userservicetests;

import dataaccess.*;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.responses.RegisterLoginResponse;
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
    public void loginValidSuccess() throws NotAuthorizedException {
        LoginRequest request = new LoginRequest("mkeyes", "123");
        RegisterLoginResponse response = userService.login(request);
        assertEquals("mkeyes", response.username());
        String expected = "{\"username\":\"mkeyes\",\"authToken\":\"" + response.authToken() + "\"}";
        String actualJson = Serializer.toJson(response);
        assertEquals(expected, actualJson);
    }

    @Test
    public void loginNotAuthorizedThrowsException() throws NotAuthorizedException {
        LoginRequest request = new LoginRequest("mke", "123");
        assertThrows(NotAuthorizedException.class, () -> userService.login(request));
    }

    @Test
    public void loginNoUsernameThrowsException() throws BadRequestException {
        LoginRequest request = new LoginRequest(null, "123");
        assertThrows(BadRequestException.class, () -> userService.login(request));
    }

    @Test
    public void loginNoPasswordThrowsException() throws BadRequestException {
        LoginRequest request = new LoginRequest("mkeyes", null);
        assertThrows(BadRequestException.class, () -> userService.login(request));
    }

    @Test
    public void loginWrongPasswordThrowsException() throws NotAuthorizedException {
        LoginRequest request = new LoginRequest("mkeyes", "123!");
        assertThrows(NotAuthorizedException.class, () -> userService.login(request));
    }
}
