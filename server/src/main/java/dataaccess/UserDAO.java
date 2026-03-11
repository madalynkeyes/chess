package dataaccess;

import dataaccess.exceptions.ResponseException;
import model.UserData;

public interface UserDAO {
    void createUser(UserData userData) throws ResponseException;

    UserData getUser(String username) throws ResponseException;

    void clear() throws ResponseException;
}
