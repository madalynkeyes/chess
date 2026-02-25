package handlers;

import io.javalin.Javalin;
import service.ClearService;
import service.Requests.ClearRequest;

public class ClearHandler {

    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Javalin javalin) {
        javalin.delete("/db", ctx ->
                HandlerUtil.handle(ctx, c -> new ClearRequest(), request -> clearService.clear()));
    }
}
