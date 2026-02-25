package service.UserServiceTests;

import dataaccess.*;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;
import service.Requests.LoginRequest;
import service.Requests.RegisterRequest;
import service.Responses.LogoutResponse;
import service.Responses.RegisterResponse;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTests {
    UserService userService;
    RegisterResponse response;
    AuthDAO authDAO = new RAMAuthDAO();
    @BeforeEach
    public void initialize(){
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        GameDAO gameDAO = new RAMGameDAO();
        userService = new UserService(userDAO,authDAO,gameDAO);
        RegisterRequest request = new RegisterRequest("mkeyes","123","m@gmail.com");
        userService.register(request);
        LoginRequest loginRequest = new LoginRequest("mkeyes","123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);
        assertNotNull(authDAO.getAuth(response.authToken()));
    }

    @Test
    public  void logout_valid_success() {
        LogoutResponse logoutResponse = userService.logout(response.authToken());
        assertEquals("{}", logoutResponse.message());
        assertNull(authDAO.getAuth(response.authToken()));
    }

    @Test
    public void logout_no_authToken() throws BadRequestException{
        assertThrows(BadRequestException.class,() -> userService.logout(null));
    }

    @Test
    public void logout_wrong_authToken() throws NotAuthorizedException{
        assertThrows(NotAuthorizedException.class,() -> userService.logout("abc"));
    }
}
