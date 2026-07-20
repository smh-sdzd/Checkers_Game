package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Complete game state including board, turn, history, scores, and players.
 * Serializable for save/load functionality.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum GameStatus { PLAYING, RED_WINS, BLACK_WINS, DRAW }

    private Board board;
    private Piece.Color currentTurn;
    private GameStatus status;
    private List<Move> moveHistory;
    private int redScore, blackScore;
    private Piece.Color localPlayerColor;
    private transient Player player1;
    private transient Player player2;
    private String gameMode; // "computer", "online", "local"

    public GameState() {
        this.board = new Board();
        this.board.initializeStandardGame();
        this.currentTurn = Piece.Color.BLACK; // Player goes first
        this.status = GameStatus.PLAYING;
        this.moveHistory = new ArrayList<>();
        this.redScore = 0;
        this.blackScore = 0;
        this.localPlayerColor = Piece.Color.BLACK;
        this.player1 = null;
        this.player2 = null;
        this.gameMode = "computer";
    }

    private GameState(Board board, Piece.Color currentTurn, GameStatus status,
                      List<Move> moveHistory, int redScore, int blackScore,
                      Piece.Color localPlayerColor, Player player1, Player player2,
                      String gameMode) {
        this.board = board;
        this.currentTurn = currentTurn;
        this.status = status;
        this.moveHistory = moveHistory;
        this.redScore = redScore;
        this.blackScore = blackScore;
        this.localPlayerColor = localPlayerColor;
        this.player1 = player1;
        this.player2 = player2;
        this.gameMode = gameMode;
    }

    public Board getBoard() { return board; }
    public Piece.Color getCurrentTurn() { return currentTurn; }
    public GameStatus getStatus() { return status; }
    public List<Move> getMoveHistory() { return new ArrayList<>(moveHistory); }
    public int getRedScore() { return redScore; }
    public int getBlackScore() { return blackScore; }
    public Piece.Color getLocalPlayerColor() { return localPlayerColor; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public String getGameMode() { return gameMode; }

    public void setPlayers(Player p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    public void setGameMode(String mode) { this.gameMode = mode; }

    public void switchTurn() {
        this.currentTurn = (this.currentTurn == Piece.Color.BLACK) ? Piece.Color.RED : Piece.Color.BLACK;
    }

    public void setStatus(GameStatus status) { this.status = status; }
    public void setLocalPlayerColor(Piece.Color color) { this.localPlayerColor = color; }

    public boolean isGameOver() { return status != GameStatus.PLAYING; }

    public Piece.Color getWinner() {
        if (status == GameStatus.RED_WINS) return Piece.Color.RED;
        if (status == GameStatus.BLACK_WINS) return Piece.Color.BLACK;
        return null;
    }

    public boolean isLocalPlayerTurn() {
        if (localPlayerColor == null) return !isGameOver();
        return !isGameOver() && currentTurn == localPlayerColor;
    }

    public void addMoveToHistory(Move move) {
        moveHistory.add(move);
        if (move.isCapture()) {
            if (currentTurn == Piece.Color.RED) redScore++;
            else blackScore++;
        }
    }

    public GameState copy() {
        return new GameState(
                board.copy(),
                currentTurn,
                status,
                new ArrayList<>(moveHistory),
                redScore,
                blackScore,
                localPlayerColor,
                player1,
                player2,
                gameMode
        );
    }
}