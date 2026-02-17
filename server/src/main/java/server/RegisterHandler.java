package server;
import io.javalin.http.Context;
import io.javalin.http.Handler;


public class RegisterHandler implements Handler {
    public void handle(Context context) {
        context.result("Hello BYU!");
    }
}
