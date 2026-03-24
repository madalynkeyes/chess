package server;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.NotFoundException;
import dataaccess.exceptions.ResponseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ClientCommunicator {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static String doPost(String url, String urlPath, String message, String authToken) throws Exception {
        String urlString = String.format("%s%s", url, urlPath);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(Duration.ofMillis(5000))
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

    public static String doPut(String url, String urlPath, String message, String authToken) throws Exception {
        String urlString = String.format("%s%s", url, urlPath);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(Duration.ofMillis(5000))
                .header("Content-Type", "application/json");

        if(authToken!=null){
            builder.header("authorization",authToken);
        }
        builder.PUT(HttpRequest.BodyPublishers.ofString(message));
        HttpRequest request = builder.build();
        return getHttpResponse(request);
    }

    public static String doGet(String url, String urlPath, String authToken) throws Exception {
        String urlString = String.format("%s%s", url, urlPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(Duration.ofMillis(5000))
                .header("authorization", authToken)
                .GET()
                .build();

        return getHttpResponse(request);
//        return urlString;
    }

    public static void doDelete(String url, String urlPath, String authToken) throws Exception {
        String urlString = String.format("%s%s", url, urlPath);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(Duration.ofMillis(5000))
                .header("authorization", authToken)
                .DELETE()
                .build();
        getHttpResponse(request);

    }

    public static String getHttpResponse(HttpRequest request) throws IOException, InterruptedException, ResponseException {
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(httpResponse.statusCode()==403){
            throw new AlreadyTakenException(httpResponse.body());
        }
        if(httpResponse.statusCode()==400){
            throw new BadRequestException(httpResponse.body());
        }
        if(httpResponse.statusCode()==404){
            throw new NotFoundException(httpResponse.body());
        }
        if(httpResponse.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError,httpResponse.body());
        }

        return httpResponse.body();
    }
}
