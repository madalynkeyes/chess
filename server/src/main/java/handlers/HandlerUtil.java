package handlers;

import dataaccess.exceptions.*;
import io.javalin.http.Context;
import server.Serializer;

import java.util.function.Function;

public class HandlerUtil {
@FunctionalInterface
public interface ServiceFunction<Req, Res> {
    Res apply(Req request) throws ResponseException;
}

    public static <Req, Res> void handle(
            Context ctx,
            Function<Context, Req> requestBuilder,
            ServiceFunction<Req, Res> serviceFunction
    ) {
        try {
            Req request = requestBuilder.apply(ctx);
            Res result = serviceFunction.apply(request);
            ctx.status(200);
            ctx.result(Serializer.toJson(result));
        } catch (ResponseException ex) {
            ctx.status(ex.code() == ResponseException.Code.ServerError ? 500 : 400);
            ctx.result("{\"message\":\"" + ex.getMessage() + "\"}");
        }catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
        }catch (BadRequestException | NotFoundException e) {
            ctx.status(400);
            ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (NotAuthorizedException e){
            ctx.status(401);
            ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}

