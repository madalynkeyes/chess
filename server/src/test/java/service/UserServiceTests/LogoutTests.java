package service.UserServiceTests;

import dataaccess.AuthDAO;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotAuthorizedException;
import dataaccess.RAMAuthDAO;
import dataaccess.RAMUserDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTests {
    UserService userService;
    RegisterResponse response;
    @BeforeEach
    public void initialize(){
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        userService = new UserService(userDAO,authDAO);
        RegisterRequest request = new RegisterRequest("mkeyes","123","m@gmail.com");
        userService.register(request);
        LoginRequest loginRequest = new LoginRequest("mkeyes","123");
        userService.login(loginRequest);
        response = userService.login(loginRequest);
    }

    @Test
    public  void logout_valid_success() {
        LogoutResponse logoutResponse = userService.logout(response.authToken());
        assertEquals("{}", logoutResponse.message());
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
