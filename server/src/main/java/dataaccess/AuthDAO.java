package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);
    AuthData getAuth(String authToken);
    String getUserByToken(String authToken);
    void clear();
    void deleteAuth(String authToken);
}
