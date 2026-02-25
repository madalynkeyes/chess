package handlers;

import io.javalin.Javalin;
import server.Serializer;
import service.requests.LoginRequest;
import service.requests.LogoutOrListGamesRequest;
import service.requests.RegisterRequest;
import service.UserService;


public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Javalin javalin) {
        javalin.post("/user", ctx ->
                HandlerUtil.handle(ctx, c -> Serializer.fromJson(c.body(), RegisterRequest.class), userService::register));
    }

    public void login(Javalin javalin) {
        javalin.post("/session", ctx ->
                HandlerUtil.handle(ctx, c -> Serializer.fromJson(c.body(), LoginRequest.class), userService::login));
    }

    public void logout(Javalin javalin) {
        javalin.delete("/session", ctx ->
                HandlerUtil.handle(ctx, c -> {
                    String authToken = c.header("Authorization");
                    return new LogoutOrListGamesRequest(authToken);
                }, userService::logout));
    }

}
