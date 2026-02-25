package service.Requests;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {
}
