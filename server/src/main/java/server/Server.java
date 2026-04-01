package server;

import dataaccess.*;
import dataaccess.exceptions.*;
//import handlers.LoginHandler;
//import handlers.RegisterHandler;
import handlers.ClearHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import io.javalin.*;
import org.jetbrains.annotations.NotNull;
import server.websocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
//        UserDAO userDAO = new RAMUserDAO();
        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;
        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (ResponseException | DataAccessException e) {
            throw new RuntimeException(e);
        }
//        AuthDAO authDAO = new RAMAuthDAO();
//        GameDAO gameDAO = new RAMGameDAO();
        InitializeServiceMethods services = getInitializeServiceMethods(userDAO, authDAO, gameDAO);
        webSocketHandler = new WebSocketHandler(authDAO, gameDAO);
        javalin.ws("/ws",ws -> {
            ws.onConnect(webSocketHandler);
            ws.onClose(webSocketHandler);
            ws.onMessage(webSocketHandler);
        });
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
//        WebSocketHandler webSocketHandler = new WebSocketHandler();

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
            ctx.status(404);
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
