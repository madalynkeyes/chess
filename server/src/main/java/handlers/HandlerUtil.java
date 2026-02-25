package handlers;

import io.javalin.http.Context;
import server.Serializer;

import java.util.function.Function;

public class HandlerUtil {
    public static <Req, Res> void handle(
            Context ctx,
            Function<Context, Req> requestBuild,
            Function<Req, Res> serviceMethod
    ) {
        Req request = requestBuild.apply(ctx);
        Res response = serviceMethod.apply(request);
        ctx.status(200);
        ctx.result(Serializer.toJson(response));

    }
}
