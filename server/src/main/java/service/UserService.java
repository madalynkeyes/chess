package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;

    }

    public RegisterResponse getUser(RegisterRequest request) throws DataAccessException {
        if(request.username()==null||request.password()==null||request.email()==null){
            throw new IllegalArgumentException("Error: enter username, password and email");
        }
        if (userDAO.getUser(request.username())!=null){
            throw new DataAccessException("Error: username already taken");
        }
        UserData userData = new UserData(
                request.username(),
                request.password(),
                request.email()
        );
        userDAO.createUser(userData);
        AuthData authData = new AuthData(
                UUID.randomUUID().toString(),
                request.username()
        );
        authDAO.createAuth(authData);
        return new RegisterResponse(
                userData.username(),
                authData.authToken()
        );

    }
}
