package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;

    }

    public RegisterResponse getUser(RegisterRequest request) throws DataAccessException {
        if (userDAO.getUser(request.username())!=null){
            throw new DataAccessException("Error: username already taken");
        }
        UserData userData = new UserData(
                request.username(),
                request.password(),
                request.email()
        );
        userDAO.createUser(userData);
        String authToken = "abc12";
        return new RegisterResponse(
                userData.username(),
                authToken
        );

    }
}
