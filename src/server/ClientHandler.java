package server;

import java.net.Socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ClientHandler {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket client) {

        this.client = client;

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

        try {

            out.println("Welcome to Checkers!");

            String message = in.readLine();

            System.out.println("Client says: " + message);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}