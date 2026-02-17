package server;

import dataaccess.RAMUserDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import service.RegisterRequest;
import service.RegisterResponse;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        UserDAO userDAO = new RAMUserDAO();
        UserService userService = new UserService(userDAO);

        // Register your endpoints and exception handlers here.
//        javalin.post("/user",new RegisterHandler());
        javalin.post("/user",ctx->{
            //deserialize JSON body request
            RegisterRequest request = Serializer.fromJson(ctx.body(),RegisterRequest.class);
            //get the right endpoint & call service class
            //record the response from the service class
            RegisterResponse response = userService.getUser(request);
            //serialize that response
            String jsonResponse = Serializer.toJson(response);
            //send success response
            ctx.status(200);
            ctx.result(jsonResponse);
        });
    }

    private void register(Context context){

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }


}
