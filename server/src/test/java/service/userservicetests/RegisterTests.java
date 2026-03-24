//package service.userservicetests;
//
//import dataaccess.*;
//import dataaccess.exceptions.AlreadyTakenException;
//import dataaccess.exceptions.BadRequestException;
//import dataaccess.exceptions.ResponseException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import server.Serializer;
//import service.requests.RegisterRequest;
//import service.responses.RegisterLoginResponse;
//import service.UserService;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class RegisterTests {
//    UserService userService;
//
//    @BeforeEach
//    public void initialize() {
//        UserDAO userDAO = new RAMUserDAO();
//        AuthDAO authDAO = new RAMAuthDAO();
//        userService = new UserService(userDAO, authDAO);
//    }
//
//    @Test
//    public void registerValidSuccess() throws ResponseException {
//        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
//        RegisterLoginResponse response = userService.register(request);
//        assertEquals("mkeyes", response.username());
//        String expected = "{\"username\":\"mkeyes\",\"authToken\":\"" + response.authToken() + "\"}";
//        String actualJson = Serializer.toJson(response);
//        assertEquals(expected, actualJson);
//    }
//
//    @Test
//    public void registerUsernameTakenThrowsException() throws AlreadyTakenException, ResponseException {
//        RegisterRequest request = new RegisterRequest("mkeyes", "123", "m@gmail.com");
//        userService.register(request);
//        RegisterRequest request2 = new RegisterRequest("mkeyes", "123", "m@gmail.com");
//        assertThrows(AlreadyTakenException.class, () -> userService.register(request2));
//    }
//
//    @Test
//    public void registerNoUsernameThrowsException() throws BadRequestException {
//        RegisterRequest request = new RegisterRequest(null, "123", "m@gmail.com");
//        assertThrows(BadRequestException.class, () -> userService.register(request));
//    }
//
//    @Test
//    public void registerNoPasswordThrowsException() throws BadRequestException {
//        RegisterRequest request = new RegisterRequest("mk", null, "m@gmail.com");
//        assertThrows(BadRequestException.class, () -> userService.register(request));
//    }
//
//    @Test
//    public void registerNoEmailThrowsException() throws BadRequestException {
//        RegisterRequest request = new RegisterRequest("mk", "123", null);
//        assertThrows(BadRequestException.class, () -> userService.register(request));
//    }
//}
