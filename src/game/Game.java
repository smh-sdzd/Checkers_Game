//package game;
//
//import server.ClientHandler;
//import common.Protocol;
//
//public class Game {
//    private ClientHandler player1;   // WHITE
//    private ClientHandler player2;   // BLACK
//    private ClientHandler currentTurn;
//
//    public Game(ClientHandler player1, ClientHandler player2) {
//        this.player1 = player1;
//        this.player2 = player2;
//        this.currentTurn = player1;   // white starts
//    }
//
//    public ClientHandler getPlayer1() { return player1; }
//    public ClientHandler getPlayer2() { return player2; }
//
//    public synchronized void start() {
//        player1.sendMessage(Protocol.COLOR + " " + Protocol.WHITE);
//        player2.sendMessage(Protocol.COLOR + " " + Protocol.BLACK);
//        player1.sendMessage(Protocol.START);
//        player2.sendMessage(Protocol.START);
//        player1.sendMessage(Protocol.YOUR_TURN);
//        player2.sendMessage(Protocol.WAIT);
//    }
//
//    public synchronized void handleMove(ClientHandler sender, String move) {
//        if (sender != currentTurn) {
//            sender.sendMessage(Protocol.INVALID);   // not your turn
//            return;
//        }
//        ClientHandler opponent = (sender == player1) ? player2 : player1;
//        opponent.sendMessage(move);      // relay the move
//        // flip turn
//        currentTurn = opponent;
//        opponent.sendMessage(Protocol.YOUR_TURN);
//        sender.sendMessage(Protocol.WAIT);
//    }
//
//    public synchronized void opponentLeft(ClientHandler leaver) {
//        ClientHandler other = (leaver == player1) ? player2 : player1;
//        if (other != null) other.sendMessage(Protocol.OPPONENT_LEFT);
//    }
//}