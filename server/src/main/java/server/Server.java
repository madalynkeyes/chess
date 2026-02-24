package server;

import dataaccess.*;
import dataaccess.Exceptions.AlreadyTakenException;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotAuthorizedException;
//import handlers.LoginHandler;
//import handlers.RegisterHandler;
import handlers.UserHandler;
import io.javalin.*;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);
        UserHandler userHandler = new UserHandler(userService);
        userHandler.register(javalin);
        userHandler.login(javalin);
        userHandler.logout(javalin);

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
