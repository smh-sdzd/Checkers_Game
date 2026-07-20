package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;



    public enum GameStatus {
        PLAYING,
        RED_WINS,
        BLACK_WINS,
        DRAW
    }


    private Board board;
    private Piece.Color currentTurn;
    private GameStatus status;
    private List<Move> moveHistory;
    private int redScore;
    private int blackScore;
    private Piece.Color localPlayerColor;


    public GameState() {
        this.board = new Board();
        this.board.initializeStandardGame();
        this.currentTurn = Piece.Color.BLACK;
        this.status = GameStatus.PLAYING;
        this.moveHistory = new ArrayList<>();
        this.redScore = 0;
        this.blackScore = 0;
        this.localPlayerColor = null; // برای شبکه ، بعدا بررسی شود
    }


    private GameState(Board board, Piece.Color currentTurn, GameStatus status,
                      List<Move> moveHistory, int redScore, int blackScore,
                      Piece.Color localPlayerColor) {
        this.board = board;
        this.currentTurn = currentTurn;
        this.status = status;
        this.moveHistory = moveHistory;
        this.redScore = redScore;
        this.blackScore = blackScore;
        this.localPlayerColor = localPlayerColor;
    }



    public Board getBoard() {
        return board;
    }

    public Piece.Color getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public int getRedScore() {
        return redScore;
    }

    public int getBlackScore() {
        return blackScore;
    }

    public Piece.Color getLocalPlayerColor() {
        return localPlayerColor;
    }




    public void switchTurn() {
        this.currentTurn = (this.currentTurn == Piece.Color.BLACK)
                ? Piece.Color.RED
                : Piece.Color.BLACK;
    }


    public void setStatus(GameStatus status) {
        this.status = status;
    }


    public void setLocalPlayerColor(Piece.Color localPlayerColor) {
        this.localPlayerColor = localPlayerColor;
    }


    public boolean isGameOver() {
        return this.status != GameStatus.PLAYING;
    }


    public Piece.Color getWinner() {
        if (this.status == GameStatus.RED_WINS) {
            return Piece.Color.RED;
        } else if (this.status == GameStatus.BLACK_WINS) {
            return Piece.Color.BLACK;
        }
        return null;
    }


    public boolean isLocalPlayerTurn() {
        if (localPlayerColor == null) {
            return !isGameOver();
        }
        return !isGameOver() && this.currentTurn == this.localPlayerColor;
    }


    public void addMoveToHistory(Move move) {
        this.moveHistory.add(move);

        if (move.isCapture()) {
            Piece.Color currentPlayer = this.currentTurn;
            if (currentPlayer == Piece.Color.RED) {
                this.redScore++;
            } else {
                this.blackScore++;
            }
        }
    }


    public int getMoveCount() {
        return moveHistory.size();
    }


    public Move getLastMove() {
        if (moveHistory.isEmpty()) {
            return null;
        }
        return moveHistory.get(moveHistory.size() - 1);
    }


    public boolean hasPieces(Piece.Color color) {
        return board.getPiecesByColor(color).size() > 0;
    }



    public GameState copy() {
        Board boardCopy = this.board.copy();

        List<Move> historyCopy = new ArrayList<>(this.moveHistory);

        return new GameState(
                boardCopy,
                this.currentTurn,
                this.status,
                historyCopy,
                this.redScore,
                this.blackScore,
                this.localPlayerColor
        );
    }

}