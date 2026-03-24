package server;

import dataaccess.exceptions.*;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.responses.*;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest;
import java.util.List;

import static server.ClientCommunicator.doPost;

public class ServerFacade {
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {

        serverUrl = url;
    }

    public String register(RegisterRequest registerRequest) throws ResponseException, URISyntaxException, IOException, InterruptedException {
        String body = Serializer.toJson(registerRequest);
        String responseBody = doPost(serverUrl,"/user",body,authToken);
        RegisterLoginResponse registerResponse = Serializer.fromJson(responseBody, RegisterLoginResponse.class);
        this.authToken = registerResponse.authToken();
        return authToken;
    }

    public String login(LoginRequest loginRequest) throws ResponseException, URISyntaxException, IOException, InterruptedException {
        String body = Serializer.toJson(loginRequest);
        String responseBody = doPost(serverUrl,"/session",body,authToken);
        RegisterLoginResponse loginResponse = Serializer.fromJson(responseBody, RegisterLoginResponse.class);
        this.authToken = loginResponse.authToken();
        return authToken;
    }

    public String logout() throws ResponseException, IOException, URISyntaxException, InterruptedException {
        ClientCommunicator.doDelete(serverUrl,"/session",authToken);
        authToken = null;
        return authToken;
    }

    public void clear() throws URISyntaxException, ResponseException, IOException, InterruptedException {
        String urlString = String.format("%s%s", serverUrl, "/db");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .DELETE()
                .build();
        ClientCommunicator.getHttpResponse(request);
    }

    public int createGame(CreateGameRequest createGameRequest) throws ResponseException, URISyntaxException, IOException, InterruptedException {
        if(authToken==null){
            throw new NotAuthorizedException("Error: Not Authorized");
        }
        String body = Serializer.toJson(createGameRequest);
        String responseBody = doPost(serverUrl,"/game",body,authToken);
        CreateGameResponse createGameResponse = Serializer.fromJson(responseBody, CreateGameResponse.class);
        return createGameResponse.gameID();
    }

    public List<GameListFormat> listGames() throws ResponseException, URISyntaxException, IOException, InterruptedException {
        if(authToken==null){
            throw new NotAuthorizedException("Error: Not Authorized");
        }
        String responseBody = ClientCommunicator.doGet(serverUrl,"/game",authToken);
        ListGamesResponse listGamesResponse = Serializer.fromJson(responseBody, ListGamesResponse.class);
        return listGamesResponse.games();
    }

    public JoinClearLogoutResponse joinGame(JoinGameRequest joinGameRequest)
            throws ResponseException, URISyntaxException, IOException, InterruptedException {
        if(authToken==null){
            throw new NotAuthorizedException("Error: Not Authorized");
        }
        String body = Serializer.toJson(joinGameRequest);
        String responseBody = ClientCommunicator.doPut(serverUrl,"/game",body,authToken);
        return Serializer.fromJson(responseBody, JoinClearLogoutResponse.class);
    }


}

