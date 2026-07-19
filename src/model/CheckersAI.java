package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Checkers AI using minimax with alpha-beta pruning.
 *
 * Relies only on the public API confirmed via GameLogic / Board:
 *   Piece.Color.RED / BLACK, isKing(), getColor(), getForwardDirection()
 *   GameLogic.getAllValidMovesForPlayer(board, color)
 *   GameLogic.applyMove(state, move)  -> returns a fresh GameState
 *   GameState.getCurrentTurn(), isGameOver(), getStatus(), getBoard()
 *
 * Because applyMove keeps the turn on the same color during a capture chain,
 * we decide maximize/minimize by reading state.getCurrentTurn() at each node.
 */
public class CheckersAI {

    public enum Difficulty {
        EASY(2),
        MEDIUM(5),
        HARD(8);

        private final int depth;

        Difficulty(int depth) {
            this.depth = depth;
        }

        public int getDepth() {
            return depth;
        }
    }

    // Evaluation weights.
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
     * Chooses the best move for the AI's color in the given state.
     * Returns null if there are no legal moves (game already lost/over).
     */
    public Move chooseMove(GameState state) {
        List<Move> moves = GameLogic.getAllValidMovesForPlayer(
                state.getBoard(), state.getCurrentTurn());

        if (moves.isEmpty()) {
            return null;
        }

        // EASY: pick a random legal move but never pass up an immediate capture.
        if (difficulty == Difficulty.EASY) {
            List<Move> captures = new ArrayList<>();
            for (Move m : moves) {
                if (m.isCapture()) captures.add(m);
            }
            List<Move> pool = captures.isEmpty() ? moves : captures;
            Collections.shuffle(pool);
            return pool.get(0);
        }

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
     * Returns the value of the state from the AI's perspective.
     * Maximizing when it is the AI's turn, minimizing otherwise.
     */
    private int minimax(GameState state, int depth, int alpha, int beta) {
        if (state.isGameOver()) {
            return terminalScore(state, depth);
        }
        if (depth == 0) {
            return evaluate(state.getBoard());
        }

        Piece.Color turn = state.getCurrentTurn();
        List<Move> moves = GameLogic.getAllValidMovesForPlayer(
                state.getBoard(), turn);

        // No legal moves: the player to move loses.
        if (moves.isEmpty()) {
            return terminalScore(state, depth);
        }

        boolean maximizing = (turn == aiColor);

        if (maximizing) {
            int value = Integer.MIN_VALUE;
            for (Move move : moves) {
                GameState child = GameLogic.applyMove(state, move);
                value = Math.max(value, minimax(child, depth - 1, alpha, beta));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break; // beta cutoff
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (Move move : moves) {
                GameState child = GameLogic.applyMove(state, move);
                value = Math.min(value, minimax(child, depth - 1, alpha, beta));
                beta = Math.min(beta, value);
                if (alpha >= beta) break; // alpha cutoff
            }
            return value;
        }
    }

    /**
     * Scores a finished game. Shallower wins (larger depth remaining) score
     * higher so the AI prefers faster wins and slower losses.
     */
    private int terminalScore(GameState state, int depth) {
        Piece.Color winner = state.getWinner();
        if (winner == null) {
            return 0; // draw
        }
        int adjusted = WIN_SCORE + depth;
        return (winner == aiColor) ? adjusted : -adjusted;
    }

    /**
     * Static board evaluation from the AI's perspective.
     * Positive favours the AI. Considers material, kings, advancement,
     * back-row defence and central control.
     */
    private int evaluate(Board board) {
        int score = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPieceAt(r, c);
                if (p == null) continue;

                int value = p.isKing() ? KING_VALUE : MAN_VALUE;
                value += positionalBonus(p, r, c);

                if (p.getColor() == aiColor) {
                    score += value;
                } else {
                    score -= value;
                }
            }
        }
        return score;
    }

    private int positionalBonus(Piece p, int row, int col) {
        int bonus = 0;

        // Central columns are more mobile.
        if (col >= 2 && col <= 5) {
            bonus += 5;
        }

        if (!p.isKing()) {
            // Reward advancement toward the promotion row.
            // RED promotes at row 7, BLACK promotes at row 0.
            if (p.getColor() == Piece.Color.RED) {
                bonus += row * 2;
                if (row == 0) bonus += 8; // holding the back row
            } else {
                bonus += (7 - row) * 2;
                if (row == 7) bonus += 8;
            }
        }

        return bonus;
    }
}
