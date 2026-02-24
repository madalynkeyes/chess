//package handlers;
//import dataaccess.Exceptions.NotAuthorizedException;
//import io.javalin.Javalin;
//import io.javalin.http.Context;
//import server.Serializer;
//import service.LoginRequest;
//import service.RegisterResponse;
//import service.UserService;
//
//
//public class LoginHandler{
//    private final UserService userService;
//
//    public LoginHandler(Javalin javalinServer, UserService userService) {
//        this.userService = userService;
//        javalinServer.post("/session",this::login);
//    }
//
//    public void login(Context ctx) {
//        try {
//            //deserialize JSON body request
//            LoginRequest request = Serializer.fromJson(ctx.body(),LoginRequest.class);
//            //get the right endpoint & call service class
//            //record the response from the service class
//            RegisterResponse response = userService.login(request);
//            //serialize that response
//            String jsonResponse = Serializer.toJson(response);
//            //send success response
//            ctx.status(200);
//            ctx.result(jsonResponse);
//        } catch (IllegalArgumentException e){
//            ctx.status(400);
//            ctx.result(Serializer.toJson("{\"message\":\"" + e.getMessage() + "\"}"));
//        } catch (NotAuthorizedException e){
//            ctx.status(403);
//            ctx.result(Serializer.toJson("{\"message\":\"" + e.getMessage() + "\"}"));
//        }
//    }
//}
//
