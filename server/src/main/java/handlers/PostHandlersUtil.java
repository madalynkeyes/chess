//package handlers;
//
//import io.javalin.http.Context;
//import server.Serializer;
//import java.util.function.Function;
//
//public class PostHandlersUtil {
//    public static <Req, Res> void handle(
//            Context ctx,
//            Class<Req> reqClass,
//            Function<Req, Res> serviceMethod
//    ){
//        Req requestBody = Serializer.fromJson(ctx.body(), reqClass);
//        Res response = serviceMethod.apply(requestBody);
//        ctx.status(200);
//        ctx.result(Serializer.toJson(response));
//    }
//
//
//}
