//package handlers;
//import dataaccess.Exceptions.DataAccessException;
//import io.javalin.Javalin;
//import io.javalin.http.Context;
//import server.Serializer;
//import service.RegisterRequest;
//import service.RegisterResponse;
//import service.UserService;
//
//
//public class RegisterHandler{
//    private final UserService userService;
//
//    public RegisterHandler(Javalin javalinServer, UserService userService) {
//        this.userService = userService;
//        javalinServer.post("/user",this::registerHandler);
//    }
//
//    public void registerHandler(Context ctx) {
//        try {
//            //deserialize JSON body request
//            RegisterRequest request = Serializer.fromJson(ctx.body(),RegisterRequest.class);
//            //get the right endpoint & call service class
//            //record the response from the service class
//            RegisterResponse response = userService.register(request);
//            //serialize that response
//            String jsonResponse = Serializer.toJson(response);
//            //send success response
//            ctx.status(200);
//            ctx.result(jsonResponse);
//        } catch (IllegalArgumentException e){
//            ctx.status(400);
//            ctx.result(Serializer.toJson("{\"message\":\"" + e.getMessage() + "\"}"));
//        } catch (DataAccessException e){
//            ctx.status(403);
//            ctx.result(Serializer.toJson("{\"message\":\"" + e.getMessage() + "\"}"));
//        }
//    }
//}
