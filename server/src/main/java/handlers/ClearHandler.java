package handlers;

import dataaccess.exceptions.ResponseException;
import io.javalin.Javalin;
import service.ClearService;
import service.requests.ClearRequest;

public class ClearHandler {

    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Javalin javalin) {
        javalin.delete("/db", ctx ->
                HandlerUtil.handle(ctx, c -> new ClearRequest(), request -> {
                    try {
                        return clearService.clear();
                    } catch (ResponseException e) {
                        throw new RuntimeException("Error: internal server error", e);
                    }
                }));
    }
}
