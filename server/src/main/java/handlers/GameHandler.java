package handlers;

import io.javalin.Javalin;
import server.Serializer;
import service.GameService;
import service.Requests.CreateGameRequest;
import service.Requests.JoinGameRequest;
import service.Requests.LogoutOrListGamesRequest;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Javalin javalin) {
        javalin.get("/game", ctx ->
                HandlerUtil.handle(ctx, c -> {
                    String authToken = c.header("Authorization");
                    return new LogoutOrListGamesRequest(authToken);
                }, gameService::listGames));
    }

    public void createGame(Javalin javalin) {
        javalin.post("/game", ctx ->
                HandlerUtil.handle(ctx, c -> {
                    String authToken = c.header("Authorization");
                    return new CreateGameRequest(authToken, Serializer.fromJson(c.body(), CreateGameRequest.class).gameName());
                }, gameService::createGame));
    }

    public void joinGame(Javalin javalin) {
        javalin.put("/game", ctx ->
                HandlerUtil.handle(ctx, c -> {
                    String authToken = c.header("Authorization");
                    String playerColor = Serializer.fromJson(c.body(), JoinGameRequest.class).playerColor();
                    int gameID = Serializer.fromJson(c.body(), JoinGameRequest.class).gameID();
                    return new JoinGameRequest(authToken, playerColor, gameID);
                }, gameService::joinGame));
    }
}
