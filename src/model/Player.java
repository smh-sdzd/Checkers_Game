package model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a player in the game with unique ID and statistics.
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String playerId;
    private String name;
    private int score;
    private int gamesPlayed;
    private int gamesWon;

    public Player(String name) {
        this.playerId = UUID.randomUUID().toString();
        this.name = name;
        this.score = 0;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
    }

    public String getPlayerId() { return playerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public void addScore(int points) { this.score += points; }
    public int getGamesPlayed() { return gamesPlayed; }
    public void incrementGamesPlayed() { this.gamesPlayed++; }
    public int getGamesWon() { return gamesWon; }
    public void incrementGamesWon() { this.gamesWon++; }

    @Override
    public String toString() {
        return name + " (ID: " + playerId.substring(0, 8) + "...)";
    }
}