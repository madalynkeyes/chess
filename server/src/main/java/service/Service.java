package service;

import dataaccess.AuthDAO;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.NotAuthorizedException;

public class Service {
    private final AuthDAO authDAO;


    public Service(AuthDAO authDAO) {
        this.authDAO = authDAO;

    }

    /**
     * checks if auth token is valid
     */
    void hasAuthToken(String authToken) {
        if (authToken == null) {
            throw new BadRequestException("Error: auth token not entered");
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new NotAuthorizedException("Error: not authorized");
        }
    }


}
