package server;

import java.net.ServerSocket;
import java.net.Socket;

import game.Game;

public class CheckersServer {

    private ServerSocket server;
    private static final int PORT = 5000;

    private ClientHandler waitingPlayer;

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

                ClientHandler handler = new ClientHandler(client, this);

                Thread thread = new Thread(handler);

                thread.start();

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void pairPlayer(ClientHandler handler) {

        if (waitingPlayer == null) {

            waitingPlayer = handler;
            handler.sendMessage(common.Protocol.WAIT);

        }
        else {

            Game game = new Game(waitingPlayer, handler);

            waitingPlayer.setGame(game);
            handler.setGame(game);

            System.out.println("Game created between two players!");

            waitingPlayer.sendMessage(common.Protocol.YOUR_TURN);
            handler.sendMessage(common.Protocol.WAIT);

            waitingPlayer = null;

        }
    }
}