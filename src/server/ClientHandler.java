package server;

import java.net.Socket;

public class ClientHandler {
    private Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }
}