package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.NotAuthorizedException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

/**
 * Service classes to register, login and logout
 */
public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;

    }

    /**
     * Register Service Class
     * To register, getUser. If user already exists, throw error.
     * Else, create a new user and create new auth token
     * @param request register request
     * @return register response
     * @throws DataAccessException if username is already taken
     * @throws IllegalArgumentException if missing username, password or email
     */
    public RegisterResponse register(RegisterRequest request) throws DataAccessException, IllegalArgumentException {
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
    public RegisterResponse login(LoginRequest request) throws IllegalArgumentException, NotAuthorizedException {
        if(request.username()==null||request.password()==null){
            throw new IllegalArgumentException("Error: enter username and password");
        }
        if (userDAO.getUser(request.username())!=null){
            String password = userDAO.getUser(request.username()).password();
            if(!request.password().equals(password)){
                throw new NotAuthorizedException("Error: unauthorized");
            }
            AuthData authData = new AuthData(
                    UUID.randomUUID().toString(),
                    request.username()
            );
            authDAO.createAuth(authData);
            return new RegisterResponse(
                    request.username(),
                    authData.authToken()
            );

        } else{
            throw new NotAuthorizedException("Error: unauthorized");
        }
    }

}
