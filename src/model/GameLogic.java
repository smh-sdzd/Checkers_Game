package model;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    /**
     * Returns all valid moves for a piece at given position.
     * Includes both normal and capture moves.
     */
    public static List<Move> getValidMovesForPiece(Board board, int row, int col) {
        List<Move> moves = new ArrayList<>();
        Piece piece = board.getPieceAt(row, col);
        if (piece == null) return moves;

        int[][] dirs = getMoveDirections(piece);
        for (int[] d : dirs) {
            int dr = d[0], dc = d[1];
            int nr = row + dr, nc = col + dc;

            // Normal move
            if (board.isValidPosition(nr, nc) && board.isEmpty(nr, nc)) {
                moves.add(new Move(row, col, nr, nc, Move.MoveType.NORMAL));
            }

            // Capture move (jump over opponent)
            int jr = row + 2 * dr, jc = col + 2 * dc;
            int mr = row + dr, mc = col + dc;
            if (board.isValidPosition(jr, jc) && board.isEmpty(jr, jc)) {
                Piece mid = board.getPieceAt(mr, mc);
                if (mid != null && mid.getColor() != piece.getColor()) {
                    moves.add(new Move(row, col, jr, jc, Move.MoveType.CAPTURE));
                }
            }
        }
        return moves;
    }

    /**
     * Returns all valid moves for a player. Capture moves are mandatory.
     */
    public static List<Move> getAllValidMovesForPlayer(Board board, Piece.Color color) {
        List<Move> all = new ArrayList<>();
        List<Move> captures = new ArrayList<>();

        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPieceAt(r, c);
                if (p != null && p.getColor() == color) {
                    for (Move m : getValidMovesForPiece(board, r, c)) {
                        all.add(m);
                        if (m.isCapture()) captures.add(m);
                    }
                }
            }

        // Mandatory capture: if any capture exists, only those are valid
        return captures.isEmpty() ? all : captures;
    }

    public static boolean hasAnyCaptureMoveForPiece(Board board, int row, int col) {
        for (Move m : getValidMovesForPiece(board, row, col))
            if (m.isCapture()) return true;
        return false;
    }

    /**
     * Executes a move and returns a new GameState.
     * Handles capture, promotion, and chain capture logic.
     */
    public static GameState applyMove(GameState state, Move move) {
        GameState newState = state.copy();
        Board board = newState.getBoard();

        Piece piece = board.getPieceAt(move.getFromRow(), move.getFromCol());
        if (piece == null) throw new IllegalArgumentException("No piece at source!");

        // Move piece
        board.removePiece(move.getFromRow(), move.getFromCol());
        board.setPieceAt(move.getToRow(), move.getToCol(), piece);

        // Handle capture
        if (move.isCapture()) {
            board.removePiece(move.getCapturedRow(), move.getCapturedCol());
            newState.addMoveToHistory(move);
        } else {
            newState.addMoveToHistory(move);
        }

        // Check for promotion
        checkAndPromote(board, move.getToRow(), move.getToCol());

        // Chain capture: if piece can capture again, don't switch turn
        boolean canCaptureAgain = hasAnyCaptureMoveForPiece(board, move.getToRow(), move.getToCol());
        if (!canCaptureAgain || !move.isCapture()) {
            newState.switchTurn();
        }

        newState.setStatus(evaluateGameStatus(newState));
        return newState;
    }

    /**
     * Promotes piece to king if it reaches the opposite end.
     * BLACK promotes at row 0 (top), RED promotes at row 7 (bottom).
     */
    private static void checkAndPromote(Board board, int row, int col) {
        Piece p = board.getPieceAt(row, col);
        if (p == null) return;
        if (p.getColor() == Piece.Color.BLACK && row == 0) p.makeKing();
        else if (p.getColor() == Piece.Color.RED && row == 7) p.makeKing();
    }

    /**
     * Evaluates game status: winner or still playing.
     */
    public static GameState.GameStatus evaluateGameStatus(GameState state) {
        Board board = state.getBoard();
        int red = board.getPieceCount(Piece.Color.RED);
        int black = board.getPieceCount(Piece.Color.BLACK);

        if (red == 0) return GameState.GameStatus.BLACK_WINS;
        if (black == 0) return GameState.GameStatus.RED_WINS;

        Piece.Color turn = state.getCurrentTurn();
        if (getAllValidMovesForPlayer(board, turn).isEmpty()) {
            return (turn == Piece.Color.RED) ? GameState.GameStatus.BLACK_WINS : GameState.GameStatus.RED_WINS;
        }
        return GameState.GameStatus.PLAYING;
    }

    /**
     * Returns movement directions based on piece type and color.
     * Normal pieces move forward only; kings move in all four diagonal directions.
     */
    private static int[][] getMoveDirections(Piece piece) {
        if (piece.isKing()) {
            return new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        } else {
            int f = piece.getForwardDirection(); // BLACK = -1, RED = +1
            return new int[][]{{f, -1}, {f, 1}};
        }
    }
}