//package handlers;
//
//import dataaccess.Exceptions.BadRequestException;
//import io.javalin.http.Context;
//import server.Serializer;
//
//import java.util.function.Function;
//
//public class GetDeleteHandlersUtil {
//    public static <T> void handle(
//            Context ctx,
//            Function<String, T> serviceMethod
//    ) {
//        String authToken = ctx.header("Authorization");
//        if (authToken == null){
//            throw new BadRequestException("Error: bad request");
//        }
//
//        T response = serviceMethod.apply(authToken);
//        ctx.status(200);
//        ctx.result(Serializer.toJson(response));
//    }
//
//
//}
