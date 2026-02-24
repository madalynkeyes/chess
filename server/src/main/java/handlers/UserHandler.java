package handlers;

import io.javalin.Javalin;
import service.LoginRequest;
import service.RegisterRequest;
import service.UserService;
import service.LogoutRequest;


public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Javalin javalin){
        javalin.post("/user",ctx ->
                PostHandlersUtil.handle(ctx, RegisterRequest.class,userService::register));
    }

    public void login(Javalin javalin){
        javalin.post("/session", ctx -> PostHandlersUtil.handle(ctx, LoginRequest.class, userService::login));
    }

    public void logout(Javalin javalin){
        javalin.delete("/session",
                ctx -> GetDeleteHandlersUtil.handle(ctx, userService::logout));
    }
}
