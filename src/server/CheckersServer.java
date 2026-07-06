package server;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class CheckersServer {
    public static void main(String[] args) {
        try {
        ServerSocket server = new ServerSocket(5000);

        System.out.println("Server started.");
        System.out.println("Waiting for a player to connect...");
        
        Socket client = server.accept();
        System.out.println("A player connected!");

        BufferedReader in =
            new BufferedReader(
                new InputStreamReader(client.getInputStream()));

        PrintWriter out =
            new PrintWriter(client.getOutputStream(), true);

        out.println("Welcome to Checkers!");

        String message = in.readLine();
        System.out.println("Client says: " + message);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}