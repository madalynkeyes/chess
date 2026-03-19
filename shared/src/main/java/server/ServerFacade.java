package server;

import dataaccess.exceptions.NotAuthorizedException;
import dataaccess.exceptions.ResponseException;
import service.requests.CreateGameRequest;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.responses.RegisterLoginResponse;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {

        serverUrl = url;
    }

    public void register(RegisterRequest registerRequest) throws Exception {
        String body = Serializer.toJson(registerRequest);
        String responseBody = doPost(serverUrl,"/user",body);
        RegisterLoginResponse registerResponse = Serializer.fromJson(responseBody, RegisterLoginResponse.class);
        this.authToken = registerResponse.authToken();
    }

    public void login(LoginRequest loginRequest) throws Exception {
        String body = Serializer.toJson(loginRequest);
        String responseBody = doPost(serverUrl,"/session",body);
        RegisterLoginResponse loginResponse = Serializer.fromJson(responseBody, RegisterLoginResponse.class);
        this.authToken = loginResponse.authToken();
    }

    public void logout() throws Exception {
        doDelete(serverUrl,"/session");
    }

    public void createGame(CreateGameRequest createGameRequest) throws Exception {
        if(authToken==null){
            throw new NotAuthorizedException("Error: Not Authorized");
        }
        String body = Serializer.toJson(createGameRequest);
//        String body = createGameRequest.gameName();
        doPost(serverUrl,"/game",body);

    }

    public String doPost(String url, String urlPath, String message) throws Exception {
        String urlString = String.format("%s%s", url, urlPath);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("Content-Type", "application/json");

        if(authToken!=null){
            builder.header("authorization",authToken);
        }
        if (message!= null) {
            builder.POST(HttpRequest.BodyPublishers.ofString(message));
        } else {
            builder.POST(HttpRequest.BodyPublishers.noBody());
        }

        HttpRequest request = builder.build();
//                .POST(BodyPublishers.ofString(message))
//                .build();

        return getHttpResponse(request);
    }

    public void doGet(String url, String urlPath) throws URISyntaxException, IOException, InterruptedException, ResponseException {
        String urlString = String.format("%s%s", url, urlPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("authorization", "abc123")
                .GET()
                .build();

        getHttpResponse(request);
    }

    public void doDelete(String url, String urlPath) throws Exception {
        String urlString = String.format("%s%s", url, urlPath);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
//                .timeout(java.time.Duration.ofMillis(5000))
                .header("authorization", authToken)
                .DELETE()
                .build();
        getHttpResponse(request);

    }

    private String getHttpResponse(HttpRequest request) throws IOException, InterruptedException, ResponseException {
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(httpResponse.statusCode() != 200) {
//            System.out.println(httpResponse.body());
//            System.out.println("Error: received status code " + httpResponse.statusCode());
//            System.out.println(httpResponse.body());
            throw new ResponseException(ResponseException.Code.ClientError,httpResponse.body());
        }

        return httpResponse.body();
    }

}

