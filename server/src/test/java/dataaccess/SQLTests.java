//package dataaccess;
//
//import dataaccess.exceptions.DataAccessException;
//import dataaccess.exceptions.ResponseException;
//import org.junit.jupiter.api.BeforeEach;
//import service.GameService;
//import service.UserService;
//import service.requests.LoginRequest;
//import service.requests.RegisterRequest;
//import service.responses.RegisterLoginResponse;
//
//public abstract class SQLTests {
//
//    @BeforeEach
//    public void baseSetup(){
//        try {
//            new SQLUserDAO().clear();
//            new SQLGameDAO().clear();
//            new SQLAuthDAO().clear();
//        } catch (ResponseException | DataAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
