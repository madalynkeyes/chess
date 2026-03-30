package client.websocket;

import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import ui.Client;

public class WebSocketFacade extends Endpoint {
    Session session;

    public WebSocketFacade(String url, Client client) {
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
