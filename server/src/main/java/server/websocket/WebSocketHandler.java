package server.websocket;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import server.Serializer;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
        @Override
        public void handleConnect(@NotNull WsConnectContext ctx) {
            ctx.enableAutomaticPings();
            System.out.println("Websocket connected");
        }

        @Override
        public void handleMessage(@NotNull WsMessageContext ctx) {
            ctx.send(Serializer.toJson("WebSocket response:" + ctx.message()));
        }

        @Override
        public void handleClose(@NotNull WsCloseContext ctx) {
            System.out.println("Websocket closed");
        }

}
