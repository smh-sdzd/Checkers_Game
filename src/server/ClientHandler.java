package server;

import java.net.Socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import common.Protocol;

public class ClientHandler implements Runnable {

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

        sendMessage(Protocol.WELCOME);

        while (true) {

            String message = receiveMessage();

            if (message == null) {
                System.out.println("Client disconnected.");
                break;
            }

            processMessage(message);
        }

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