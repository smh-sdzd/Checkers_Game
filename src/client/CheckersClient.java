package client;

import java.net.Socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import common.Protocol;

public class CheckersClient {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        CheckersClient checkersClient = new CheckersClient();
        checkersClient.start();

    }

    private void start() {

        connect();

        sendMessage("Hello Server!");

        String message = receiveMessage();

        if (Protocol.WELCOME.equals(message)) {
            System.out.println("Server welcomed me!");
        }

        else {
            System.out.println("Unknown message: " + message);
        }
    }

    private void connect() {

        try {

            client = new Socket(HOST, PORT);

            System.out.println("Connected to the server!");

            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));

            out = new PrintWriter(
                    client.getOutputStream(), true);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String receiveMessage() {

        try {

            return in.readLine();

        }
        catch (Exception e) {

            e.printStackTrace();
            return null;

        }
    }
}