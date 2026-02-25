package service.UserServiceTests;

import dataaccess.*;
import dataaccess.Exceptions.AlreadyTakenException;
import dataaccess.Exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Serializer;
import service.Requests.RegisterRequest;
import service.Responses.RegisterLoginResponse;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests {
    UserService userService;

    @BeforeEach
    public void initialize() {
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void register_valid_success() {
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        RegisterLoginResponse response = userService.register(request);
        assertEquals("mkeyes", response.username());
        String expected = "{\"username\":\"mkeyes\",\"authToken\":\"" + response.authToken() + "\"}";
        String actualJson = Serializer.toJson(response);
        assertEquals(expected, actualJson);
    }

    @Test
    public void register_username_taken_throwsException() throws AlreadyTakenException {
        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        userService.register(request);
        RegisterRequest request2 = new RegisterRequest("mkeyes", "123", "m@gmail.com");
        assertThrows(AlreadyTakenException.class, () -> userService.register(request2));
    }

    @Test
    public void register_noUsername_throwsException() throws BadRequestException {
        RegisterRequest request = new RegisterRequest(null, "123", "m@gmail.com");
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    public void register_noPassword_throwsException() throws BadRequestException {
        RegisterRequest request = new RegisterRequest("mk", null, "m@gmail.com");
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    public void register_noEmail_throwsException() throws BadRequestException {
        RegisterRequest request = new RegisterRequest("mk", "123", null);
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }
}
