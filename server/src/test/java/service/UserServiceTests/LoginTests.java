package service.UserServiceTests;
import dataaccess.*;
import dataaccess.Exceptions.DataAccessException;
import dataaccess.Exceptions.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LoginRequest;
import service.RegisterRequest;
import service.RegisterResponse;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;
public class LoginTests {
    UserService userService;
    @BeforeEach
    public void initialize(){
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        userService = new UserService(userDAO,authDAO);
        RegisterRequest request = new RegisterRequest("mkeyes","123","m@gmail.com");
        userService.register(request);
    }

    @Test
    public  void login_valid_success() throws NotAuthorizedException {
        LoginRequest request = new LoginRequest("mkeyes","123");
        RegisterResponse response = userService.login(request);
        assertEquals("mkeyes",response.username());
        assertNotNull(response.authToken());
    }

    @Test
    public void login_notAuthorized_throwsException() throws NotAuthorizedException{
       LoginRequest request = new LoginRequest("mke","123");
        assertThrows(NotAuthorizedException.class,() -> userService.login(request));
    }

    @Test
    public void login_noUsername_throwsException() throws IllegalArgumentException {
        LoginRequest request = new LoginRequest(null,"123");
        assertThrows(IllegalArgumentException.class,() -> userService.login(request));
    }

    @Test
    public void login_noPassword_throwsException() throws IllegalArgumentException{
        LoginRequest request = new LoginRequest("mkeyes",null);
        assertThrows(IllegalArgumentException.class,() -> userService.login(request));
    }

    @Test
    public void login_wrongPassword_throwsException() throws NotAuthorizedException{
        LoginRequest request = new LoginRequest("mkeyes","123!");
        assertThrows(NotAuthorizedException.class,() -> userService.login(request));
    }
}
