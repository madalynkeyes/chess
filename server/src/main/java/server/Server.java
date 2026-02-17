package server;

import dataaccess.*;
import handlers.RegisterHandler;
import io.javalin.*;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        UserDAO userDAO = new RAMUserDAO();
        AuthDAO authDAO = new RAMAuthDAO();
        UserService userService = new UserService(userDAO,authDAO);
        new RegisterHandler(javalin,userService);

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
