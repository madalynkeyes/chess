package handlers;

import io.javalin.http.Context;
import server.Serializer;

import java.util.function.Function;

public class HandlerUtil {
    public static <T, R> void handle(
            Context ctx,
            Function<Context, T> requestBuild,
            Function<T, R> serviceMethod
    ) {
        T request = requestBuild.apply(ctx);
        R response = serviceMethod.apply(request);
        ctx.status(200);
        ctx.result(Serializer.toJson(response));

    }
}

