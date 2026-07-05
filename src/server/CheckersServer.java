package server;

import java.net.ServerSocket;
import java.net.Socket;

public class CheckersServer {
    public static void main(String[] args) {
        try {
        ServerSocket server = new ServerSocket(5000);
        System.out.println("Server is waiting for a player...");
        }
        catch (Exception e) {
        e.printStackTrace();
        }
    }
}