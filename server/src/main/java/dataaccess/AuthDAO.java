package dataaccess;

import dataaccess.exceptions.ResponseException;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData) throws ResponseException;

    AuthData getAuth(String authToken) throws ResponseException;

    String getUserByToken(String authToken) throws ResponseException;

    void clear() throws ResponseException;

    void deleteAuth(String authToken) throws ResponseException;
}
