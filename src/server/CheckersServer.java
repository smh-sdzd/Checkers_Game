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