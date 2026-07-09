package game;

import server.ClientHandler;

public class Game {

    private ClientHandler player1;
    private ClientHandler player2;

    public Game(ClientHandler player1, ClientHandler player2) {

        this.player1 = player1;
        this.player2 = player2;

    }

    public ClientHandler getPlayer1() {
        return player1;
    }

    public ClientHandler getPlayer2() {
        return player2;
    }

    public void sendToOpponent(ClientHandler sender, String move) {
        if (sender == player1) {
            player2.sendMessage(move);
        } else {
            player1.sendMessage(move);
        }
    }
}