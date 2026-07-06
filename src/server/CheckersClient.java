package server;

import java.net.Socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class CheckersClient {
    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 5000);

            System.out.println("Connected to the server!");

            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(client.getInputStream()));

            PrintWriter out =
                new PrintWriter(client.getOutputStream(), true);
            
            out.println("Hello Server!");
            
            String message = in.readLine();
            System.out.println(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
