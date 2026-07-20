package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Checkers AI using minimax with alpha-beta pruning.
 * Three difficulty levels: EASY (depth 2), MEDIUM (depth 5), HARD (depth 8).
 */
public class CheckersAI {

    public enum Difficulty {
        EASY(2),
        MEDIUM(5),
        HARD(8);

        private final int depth;
        Difficulty(int depth) { this.depth = depth; }
        public int getDepth() { return depth; }
    }

    private static final int MAN_VALUE = 100;
    private static final int KING_VALUE = 175;
    private static final int WIN_SCORE = 1_000_000;

    private final Piece.Color aiColor;
    private final Difficulty difficulty;

    public CheckersAI(Piece.Color aiColor, Difficulty difficulty) {
        this.aiColor = aiColor;
        this.difficulty = difficulty;
    }

    /**
     * Returns the best move for the AI based on difficulty level.
     * EASY: random move with priority on captures.
     * MEDIUM/HARD: minimax search with alpha-beta pruning.
     */
    public Move chooseMove(GameState state) {
        List<Move> moves = GameLogic.getAllValidMovesForPlayer(
                state.getBoard(), state.getCurrentTurn());

        if (moves.isEmpty()) return null;

        // EASY: random but prioritize captures
        if (difficulty == Difficulty.EASY) {
            List<Move> captures = new ArrayList<>();
            for (Move m : moves) if (m.isCapture()) captures.add(m);
            List<Move> pool = captures.isEmpty() ? moves : captures;
            Collections.shuffle(pool);
            return pool.get(0);
        }

        // Search for best move using minimax
        Move bestMove = moves.get(0);
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (Move move : moves) {
            GameState child = GameLogic.applyMove(state, move);
            int score = minimax(child, difficulty.getDepth() - 1, alpha, beta);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
            alpha = Math.max(alpha, bestScore);
        }
        return bestMove;
    }

    /**
     * Minimax algorithm with alpha-beta pruning.
     * Maximizing when it's AI's turn, minimizing otherwise.
     */
    private int minimax(GameState state, int depth, int alpha, int beta) {
        if (state.isGameOver()) return terminalScore(state, depth);
        if (depth == 0) return evaluate(state.getBoard());

        Piece.Color turn = state.getCurrentTurn();
        List<Move> moves = GameLogic.getAllValidMovesForPlayer(state.getBoard(), turn);
        if (moves.isEmpty()) return terminalScore(state, depth);

        boolean maximizing = (turn == aiColor);

        if (maximizing) {
            int value = Integer.MIN_VALUE;
            for (Move move : moves) {
                GameState child = GameLogic.applyMove(state, move);
                value = Math.max(value, minimax(child, depth - 1, alpha, beta));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break;
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (Move move : moves) {
                GameState child = GameLogic.applyMove(state, move);
                value = Math.min(value, minimax(child, depth - 1, alpha, beta));
                beta = Math.min(beta, value);
                if (alpha >= beta) break;
            }
            return value;
        }
    }

    /**
     * Scores terminal states: win with fewer moves is better.
     */
    private int terminalScore(GameState state, int depth) {
        Piece.Color winner = state.getWinner();
        if (winner == null) return 0;
        int adjusted = WIN_SCORE + depth;
        return (winner == aiColor) ? adjusted : -adjusted;
    }

    /**
     * Evaluates board position from AI's perspective.
     * Considers material, position, and advancement.
     */
    private int evaluate(Board board) {
        int score = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPieceAt(r, c);
                if (p == null) continue;
                int value = p.isKing() ? KING_VALUE : MAN_VALUE;
                value += positionalBonus(p, r, c);
                if (p.getColor() == aiColor) score += value;
                else score -= value;
            }
        }
        return score;
    }

    /**
     * Adds positional bonuses: central control and advancement toward promotion.
     */
    private int positionalBonus(Piece p, int row, int col) {
        int bonus = 0;
        if (col >= 2 && col <= 5) bonus += 5; // central columns

        if (!p.isKing()) {
            if (p.getColor() == Piece.Color.RED) {
                bonus += (7 - row) * 2; // RED promotes at row 7
                if (row == 7) bonus += 8;
            } else {
                bonus += row * 2; // BLACK promotes at row 0
                if (row == 0) bonus += 8;
            }
        }
        return bonus;
    }
}