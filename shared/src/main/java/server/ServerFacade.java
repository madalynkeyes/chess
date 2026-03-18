package server;

import com.google.gson.Gson;
import dataaccess.exceptions.ResponseException;
import model.UserData;
import service.requests.RegisterRequest;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Optional;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {

        serverUrl = url;
    }

    public void register(RegisterRequest registerRequest) throws ResponseException, URISyntaxException, IOException, InterruptedException {
//        var request = buildRequest("POST", "/user", userData);
//        System.out.println("built request");
//        var response = sendRequest(request);
//        System.out.println("sent request");
//        return handleResponse(response, UserData.class);
        String body = Serializer.toJson(registerRequest);
        System.out.printf("The body: %s\n", body);
        doPost(serverUrl,"/user",body);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        System.out.println("building request...");
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(Serializer.toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            System.out.println("Sending request...");
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        System.out.println("Evaluating response");
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new ResponseException(ResponseException.Code.ClientError,"Error: response not received");
            }

            throw new ResponseException(ResponseException.Code.ClientError, "other failure: ");
        }

        if (responseClass != null) {
            System.out.println("Received response");
            return new Serializer().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public void doPost(String url, String urlPath, String message) throws URISyntaxException, IOException, InterruptedException {
        String urlString = String.format("%s%s", url, urlPath);
        System.out.println("Sending POST to: " + urlString);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(httpResponse.statusCode() == 200) {
            HttpHeaders headers = httpResponse.headers();
            Optional<String> lengthHeader = headers.firstValue("Content-Length");

            System.out.printf("Received %s bytes%n", lengthHeader.orElse("unknown"));
            System.out.println(httpResponse.body());
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
            System.out.println(httpResponse.body());
        }
    }

}

