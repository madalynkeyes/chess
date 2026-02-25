package server;

import dataaccess.*;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.NotAuthorizedException;
//import handlers.LoginHandler;
//import handlers.RegisterHandler;
import dataaccess.exceptions.NotFoundException;
import handlers.ClearHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import io.javalin.*;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        GameDAO gameDAO = new RAMGameDAO();
        InitializeServiceMethods services = getInitializeServiceMethods(userDAO, authDAO, gameDAO);
        createHandlers(services);
        checkGlobalExceptions();

        // Register your endpoints and exception handlers here.

    }

    private void createHandlers(InitializeServiceMethods services) {
        services.userHandler().register(javalin);
        services.userHandler().login(javalin);
        services.userHandler().logout(javalin);
        services.gameHandler().listGames(javalin);
        services.gameHandler().createGame(javalin);
        services.gameHandler().joinGame(javalin);
        services.clearHandler().clear(javalin);
    }

    @NotNull
    private static InitializeServiceMethods getInitializeServiceMethods(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        UserService userService = new UserService(userDAO, authDAO);
        UserHandler userHandler = new UserHandler(userService);
        GameService gameService = new GameService(authDAO, gameDAO);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);
        return new InitializeServiceMethods(userHandler, gameHandler, clearHandler);
    }

    private record InitializeServiceMethods(UserHandler userHandler, GameHandler gameHandler,
                                            ClearHandler clearHandler) {
    }

    private void checkGlobalExceptions() {
        //global exceptions
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
        });

        javalin.exception(NotAuthorizedException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
        });

        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
        });

        javalin.exception(NotFoundException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
        });
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }


}
