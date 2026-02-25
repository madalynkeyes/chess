package service.UserServiceTests;
import dataaccess.*;
import dataaccess.Exceptions.AlreadyTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Requests.RegisterRequest;
import service.Responses.RegisterResponse;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;
public class RegisterTests {
    UserService userService;
    @BeforeEach
    public void initialize(){
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        GameDAO gameDAO = new RAMGameDAO();
        userService = new UserService(userDAO,authDAO,gameDAO);
    }

    @Test
    public  void register_valid_success(){
        RegisterRequest request = new RegisterRequest("mkeyes","123","m@gmail.com");
        RegisterResponse response = userService.register(request);
        assertEquals("mkeyes",response.username());
        assertNotNull(response.authToken());
    }

    @Test
    public void register_username_taken_throwsException() throws AlreadyTakenException {
        RegisterRequest request = new RegisterRequest("mkeyes","123","m@gmail.com");
        userService.register(request);
        RegisterRequest request2 = new RegisterRequest("mkeyes","123","m@gmail.com");
        assertThrows(AlreadyTakenException.class,() -> userService.register(request2));
    }

    @Test
    public void register_noUsername_throwsException() throws IllegalArgumentException {
        RegisterRequest request = new RegisterRequest(null,"123","m@gmail.com");
        assertThrows(IllegalArgumentException.class,() -> userService.register(request));
    }

    @Test
    public void register_noPassword_throwsException() throws IllegalArgumentException{
        RegisterRequest request = new RegisterRequest("mk",null,"m@gmail.com");
        assertThrows(IllegalArgumentException.class,() -> userService.register(request));
    }
    @Test
    public void register_noEmail_throwsException() throws IllegalArgumentException {
        RegisterRequest request = new RegisterRequest("mk","123",null);
        assertThrows(IllegalArgumentException.class,() -> userService.register(request));
    }
}
