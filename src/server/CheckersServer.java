package server;

import java.net.ServerSocket;
import java.net.Socket;

public class CheckersServer {

    private ServerSocket server;
    private static final int PORT = 5000;

    public static void main(String[] args) {

        CheckersServer checkersServer = new CheckersServer();
        checkersServer.startServer();

    }

    public void startServer() {

        try {

            server = new ServerSocket(PORT);

            System.out.println("Server started.");
            System.out.println("Waiting for a player to connect...");

            acceptClients();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void acceptClients() {

        try {

            while (true) {

                Socket client = server.accept();

                System.out.println("A player connected!");

                ClientHandler handler = new ClientHandler(client);

                handler.handleClient();

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}