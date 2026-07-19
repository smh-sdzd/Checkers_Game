package server;

import java.net.Socket;

import common.Protocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import game.Game;

public class ClientHandler implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;
    private CheckersServer server;
    private Game game;


    public ClientHandler(Socket client, CheckersServer server) {

        this.client = client;
        this.server = server;

        try {

            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));

            out = new PrintWriter(
                    client.getOutputStream(), true);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleClient() {
        sendMessage(Protocol.WELCOME);
        server.pairPlayer(this);
        while (true) {
            String message = receiveMessage();
            if (message == null) {
                System.out.println("Client disconnected.");
                break;
            }
            processMessage(message);
        }
        if (game != null) {
            game.opponentLeft(this);   // notify the other player
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void sendMessage(String message){
        out.println(message);
    }

    public String receiveMessage(){
        try {
            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void processMessage(String message) {
        if (message.equals(Protocol.CREATE)) {
            String code = server.createRoom(this);
            sendMessage(Protocol.ROOM + " " + code);   // tell player 1 their code
            return;
        }
        if (message.startsWith(Protocol.JOIN + " ")) {
            String code = message.substring(5).trim();
            boolean ok = server.joinRoom(code, this);
            if (!ok) sendMessage(Protocol.BAD_ROOM);
            return;
        }
        if (message.startsWith(Protocol.MOVE)) {
            if (game != null) game.handleMove(this, message);
            return;
        }

        if (message.startsWith(Protocol.MOVE)) {
            if (game != null) {
                game.handleMove(this, message);   // was sendToOpponent
            }
            return;
        }
        switch (message) {
            case Protocol.JOIN:
                System.out.println("Player joined.");
                break;
            case Protocol.QUIT:
                System.out.println("Player quit.");
                break;
            default:
                System.out.println("Client says: " + message);
                break;
        }
    }


    @Override
    public void run() {
        handleClient();
    }
}