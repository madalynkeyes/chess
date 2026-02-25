package server;

import dataaccess.*;
import dataaccess.Exceptions.AlreadyTakenException;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotAuthorizedException;
//import handlers.LoginHandler;
//import handlers.RegisterHandler;
import dataaccess.Exceptions.NotFoundException;
import handlers.GameHandler;
import handlers.UserHandler;
import io.javalin.*;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        GameDAO gameDAO = new RAMGameDAO();
        UserService userService = new UserService(userDAO,authDAO,gameDAO);
        UserHandler userHandler = new UserHandler(userService);
        GameService gameService = new GameService(userDAO,authDAO,gameDAO);
        GameHandler gameHandler = new GameHandler(gameService);
        userHandler.register(javalin);
        userHandler.login(javalin);
        userHandler.logout(javalin);
        gameHandler.listGames(javalin);
        gameHandler.createGame(javalin);
        gameHandler.joinGame(javalin);

        //global exceptions
        javalin.exception(AlreadyTakenException.class,(e,ctx)->{
            ctx.status(400);
            ctx.result(Serializer.toJson("{\"message\":\"" + e.getMessage() + "\"}"));
        });

        javalin.exception(NotAuthorizedException.class,(e, ctx)->{
            ctx.status(401);
            ctx.result(Serializer.toJson("{\"message\":\"" + e.getMessage() + "\"}"));
        });

        javalin.exception(BadRequestException.class,(e,ctx)->{
            ctx.status(400);
            ctx.result(Serializer.toJson("{\"message\":\"" + e.getMessage() + "\"}"));
        });

        javalin.exception(NotFoundException.class,(e,ctx)->{
            ctx.status(400);
            ctx.result(Serializer.toJson("{\"message\":\"" + e.getMessage() + "\"}"));
        });
//        new RegisterHandler(javalin,userService);
//        new LoginHandler(javalin,userService);

        // Register your endpoints and exception handlers here.

    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }


}
