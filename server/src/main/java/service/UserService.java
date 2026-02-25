package service;

import dataaccess.AuthDAO;
import dataaccess.Exceptions.AlreadyTakenException;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotAuthorizedException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.Requests.LoginRequest;
import service.Requests.LogoutOrListGamesRequest;
import service.Requests.RegisterRequest;
import service.Responses.JoinClearLogoutResponse;
import service.Responses.RegisterLoginResponse;

import java.util.UUID;

/**
 * Service classes to register, login and logout
 */
public class UserService extends Service {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        super(authDAO);
        this.userDAO = userDAO;
        this.authDAO = authDAO;

    }

    /**
     * Register Service Class
     * To register, getUser. If user already exists, throw error.
     * Else, create a new user and create new auth token
     *
     * @param request register request
     * @return register response
     * @throws BadRequestException   if missing username, password or email
     * @throws AlreadyTakenException if username is already taken
     */
    public RegisterLoginResponse register(RegisterRequest request) throws BadRequestException, AlreadyTakenException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException("Error: enter username, password and email");
        }
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Error: username already taken");
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
        return new RegisterLoginResponse(
                userData.username(),
                authData.authToken()
        );

    }

    /**
     * Login Service Class
     * To log in, getUser and if user exists, create new auth token.
     *
     * @param request login request
     * @return login response
     * @throws BadRequestException    if username or password not entered
     * @throws NotAuthorizedException if username or password doesn't match existing users in database
     */
    public RegisterLoginResponse login(LoginRequest request) throws BadRequestException, NotAuthorizedException {
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException("Error: enter username and password");
        }
        if (userDAO.getUser(request.username()) != null) {
            String password = userDAO.getUser(request.username()).password();
            if (!request.password().equals(password)) {
                throw new NotAuthorizedException("Error: unauthorized");
            }
            AuthData authData = new AuthData(
                    UUID.randomUUID().toString(),
                    request.username()
            );
            authDAO.createAuth(authData);
            return new RegisterLoginResponse(
                    request.username(),
                    authData.authToken()
            );

        } else {
            throw new NotAuthorizedException("Error: unauthorized");
        }
    }

    /**
     * Logout Service Class
     * Take an auth token, if it is valid, delete the associated auth data.
     *
     * @param request logout request
     * @return logout response
     */
    public JoinClearLogoutResponse logout(LogoutOrListGamesRequest request) {
        hasAuthToken(request.authToken());
        authDAO.deleteAuth(request.authToken());
        return new JoinClearLogoutResponse("{}");

    }


}
