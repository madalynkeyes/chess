package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RAMAuthDAO implements AuthDAO {

        private final Map<String, AuthData> authTokens = new HashMap<>();
        @Override
        public void createAuth(AuthData authData){
            String authToken = UUID.randomUUID().toString();
            authTokens.put(authToken,authData);
        }
        @Override
        public AuthData getAuth(String authToken){
            return authTokens.get(authToken);
        }

        @Override
        public void clear() {
            authTokens.clear();
        }
}
