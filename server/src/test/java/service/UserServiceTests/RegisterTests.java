package service.UserServiceTests;
import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RegisterRequest;
import service.RegisterResponse;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;
public class RegisterTests {
    UserService userService;
    @BeforeEach
    public void initialize(){
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        userService = new UserService(userDAO,authDAO);
    }

    @Test
    public  void register_valid_success() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("mkeyes","123","m@gmail.com");
        RegisterResponse response = userService.getUser(request);
        assertEquals("mkeyes",response.username());
        assertNotNull(response.authToken());
    }

    @Test
    public void register_username_taken_throwsException() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("mkeyes","123","m@gmail.com");
        userService.getUser(request);
        RegisterRequest request2 = new RegisterRequest("mkeyes","123","m@gmail.com");
        assertThrows(DataAccessException.class,() -> userService.getUser(request2));
    }

    @Test
    public void register_noUsername_throwsException() throws IllegalArgumentException {
        RegisterRequest request = new RegisterRequest(null,"123","m@gmail.com");
        assertThrows(IllegalArgumentException.class,() -> userService.getUser(request));
    }

    @Test
    public void register_noPassword_throwsException() throws IllegalArgumentException{
        RegisterRequest request = new RegisterRequest("mk",null,"m@gmail.com");
        assertThrows(IllegalArgumentException.class,() -> userService.getUser(request));
    }
    @Test
    public void register_noEmail_throwsException() throws IllegalArgumentException {
        RegisterRequest request = new RegisterRequest("mk","123",null);
        assertThrows(IllegalArgumentException.class,() -> userService.getUser(request));
    }
}
