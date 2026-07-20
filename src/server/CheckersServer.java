// package server;

// import java.net.ServerSocket;
// import java.net.Socket;
// import java.util.Map;
// import java.util.UUID;
// import java.util.concurrent.ConcurrentHashMap;

// import game.Game;

// public class CheckersServer {

//     private ServerSocket server;
//     private static final int PORT = 12345;

//     private ClientHandler waitingPlayer;

//     private final Map<String, ClientHandler> rooms = new ConcurrentHashMap<>();

//     public static void main(String[] args) {

//         CheckersServer checkersServer = new CheckersServer();
//         checkersServer.startServer();

//     }

//     public void startServer() {

//         try {

//             server = new ServerSocket(PORT);

//             System.out.println("Server started.");
//             System.out.println("Waiting for a player to connect...");

//             acceptClients();

//         }
//         catch (Exception e) {
//             e.printStackTrace();
//         }

//     }

//     private void acceptClients() {

//         try {

//             while (true) {

//                 Socket client = server.accept();

//                 System.out.println("A player connected!");

//                 ClientHandler handler = new ClientHandler(client, this);

//                 Thread thread = new Thread(handler);

//                 thread.start();

//             }

//         }
//         catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     public synchronized void pairPlayer(ClientHandler handler) {
//         if (waitingPlayer == null) {
//             waitingPlayer = handler;
//             handler.sendMessage(common.Protocol.WAIT);
//         } else {
//             Game game = new Game(waitingPlayer, handler);
//             waitingPlayer.setGame(game);
//             handler.setGame(game);
//             System.out.println("Game created between two players!");
//             game.start();          // sends colors, START, and first turn
//             waitingPlayer = null;
//         }
//     }

//     public synchronized String createRoom(ClientHandler handler) {
//         String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
//         rooms.put(code, handler);
//         return code;
//     }

//     public synchronized boolean joinRoom(String code, ClientHandler handler) {
//         ClientHandler creator = rooms.remove(code);
//         if (creator == null) return false;
//         Game game = new Game(creator, handler);
//         creator.setGame(game);
//         handler.setGame(game);
//         game.start();
//         return true;
//     }

// }
