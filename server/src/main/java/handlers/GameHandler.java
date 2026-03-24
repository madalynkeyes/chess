package handlers;

import dataaccess.exceptions.BadRequestException;
import io.javalin.Javalin;
import server.Serializer;
import service.GameService;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.LogoutOrListGamesRequest;

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
                    JoinGameRequest request = Serializer.fromJson(c.body(), JoinGameRequest.class);
                    String playerColor = request.playerColor();
                    Integer gameID = request.gameID();
                    if (playerColor==null || request.gameID() ==null){
                        throw new BadRequestException("Error: gameID or player color null");
                    }
                    return new JoinGameRequest(authToken, playerColor, gameID);
                }, gameService::joinGame));
    }
}
