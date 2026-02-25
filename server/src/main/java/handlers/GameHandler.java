package handlers;

import io.javalin.Javalin;
import server.Serializer;
import service.GameService;
import service.Requests.CreateGameRequest;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Javalin javalin) {
        javalin.get("/game", ctx ->
                HandlerUtil.handle(ctx, c -> c.header("Authorization"), gameService::listGames));
    }

    public void createGame(Javalin javalin) {
        javalin.post("/game", ctx ->
                HandlerUtil.handle(ctx, c -> {
                    String authToken = c.header("Authorization");
                    return new CreateGameRequest(authToken, Serializer.fromJson(c.body(), CreateGameRequest.class).gameName());
                }, gameService::createGame));
    }
}
