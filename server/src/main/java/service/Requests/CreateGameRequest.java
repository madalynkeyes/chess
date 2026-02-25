package service.Requests;

public record CreateGameRequest(String authToken, String gameName) {
}
