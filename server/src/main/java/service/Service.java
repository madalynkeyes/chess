package service;

import dataaccess.AuthDAO;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotAuthorizedException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class Service {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;


    public Service(UserDAO userDAO,AuthDAO authDAO,GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;

    }
    void hasAuthToken(String authToken) {
        if(authToken ==null){
            throw new BadRequestException("Error: auth token not entered");
        }
        if(authDAO.getAuth(authToken)==null){
            throw new NotAuthorizedException("Error: not authorized");
        }
    }


}
