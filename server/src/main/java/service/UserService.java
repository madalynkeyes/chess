package service;

import dataaccess.AuthDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.UserDAO;
import dataaccess.exceptions.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.requests.LoginRequest;
import service.requests.LogoutOrListGamesRequest;
import service.requests.RegisterRequest;
import service.responses.JoinClearLogoutResponse;
import service.responses.RegisterLoginResponse;

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
    public RegisterLoginResponse register(RegisterRequest request) throws BadRequestException, AlreadyTakenException, ResponseException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException("Error: enter username, password and email");
        }
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }
        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        UserData userData = new UserData(
                request.username(),
                hashedPassword,
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
    public RegisterLoginResponse login(LoginRequest request) throws BadRequestException, NotAuthorizedException, ResponseException {
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException("Error: enter username and password");
        }
        if (userDAO.getUser(request.username()) != null) {
            String password = userDAO.getUser(request.username()).password();
            if (!BCrypt.checkpw(request.password(),password)) {
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
    public JoinClearLogoutResponse logout(LogoutOrListGamesRequest request) throws ResponseException {
        hasAuthToken(request.authToken());
        authDAO.deleteAuth(request.authToken());
        return new JoinClearLogoutResponse("{}");

    }


}
