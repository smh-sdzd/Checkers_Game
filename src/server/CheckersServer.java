package server;

import java.net.ServerSocket;
import java.net.Socket;

public class CheckersServer {
    public static void main(String[] args) {
        try {
        ServerSocket server = new ServerSocket(5000);
        
        System.out.println("Server started.");
        System.out.println("Waiting for a player to connect...");
        
        Socket client = server.accept();
        System.out.println("A player connected!");

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}