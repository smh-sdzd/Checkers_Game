package model;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    public static List<Move> getValidMovesForPiece(Board board, int row, int col) {
        List<Move> moves = new ArrayList<>();
        Piece piece = board.getPieceAt(row, col);
        if (piece == null) return moves;

        int[][] directions = getMoveDirections(piece);

        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];
            int newRow = row + dr;
            int newCol = col + dc;

            if (board.isValidPosition(newRow, newCol) && board.isEmpty(newRow, newCol)) {
                moves.add(new Move(row, col, newRow, newCol, Move.MoveType.NORMAL));
            }

            int jumpRow = row + 2 * dr;
            int jumpCol = col + 2 * dc;
            int middleRow = row + dr;
            int middleCol = col + dc;

            if (board.isValidPosition(jumpRow, jumpCol) && board.isEmpty(jumpRow, jumpCol)) {
                Piece middlePiece = board.getPieceAt(middleRow, middleCol);
                if (middlePiece != null && middlePiece.getColor() != piece.getColor()) {
                    moves.add(new Move(row, col, jumpRow, jumpCol, Move.MoveType.CAPTURE));
                }
            }
        }
        return moves;
    }

    public static List<Move> getAllValidMovesForPlayer(Board board, Piece.Color color) {
        List<Move> allMoves = new ArrayList<>();
        List<Move> captureMoves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPieceAt(r, c);
                if (p != null && p.getColor() == color) {
                    List<Move> pieceMoves = getValidMovesForPiece(board, r, c);
                    for (Move move : pieceMoves) {
                        allMoves.add(move);
                        if (move.isCapture()) captureMoves.add(move);
                    }
                }
            }
        }

        if (!captureMoves.isEmpty()) return captureMoves;
        return allMoves;
    }

    public static boolean hasAnyCaptureMoveForPiece(Board board, int row, int col) {
        List<Move> moves = getValidMovesForPiece(board, row, col);
        for (Move move : moves) {
            if (move.isCapture()) return true;
        }
        return false;
    }

    public static GameState applyMove(GameState state, Move move) {
        GameState newState = state.copy();
        Board board = newState.getBoard();

        Piece piece = board.getPieceAt(move.getFromRow(), move.getFromCol());
        if (piece == null) throw new IllegalArgumentException("No piece at source position!");

        board.removePiece(move.getFromRow(), move.getFromCol());
        board.setPieceAt(move.getToRow(), move.getToCol(), piece);

        if (move.isCapture()) {
            int capturedRow = move.getCapturedRow();
            int capturedCol = move.getCapturedCol();
            board.removePiece(capturedRow, capturedCol);
            newState.addMoveToHistory(move);
        } else {
            newState.addMoveToHistory(move);
        }

        checkAndPromote(board, move.getToRow(), move.getToCol());

        boolean canCaptureAgain = hasAnyCaptureMoveForPiece(board, move.getToRow(), move.getToCol());
        if (!canCaptureAgain) {
            newState.switchTurn();
        }

        GameState.GameStatus newStatus = evaluateGameStatus(newState);
        newState.setStatus(newStatus);
        return newState;
    }

    private static void checkAndPromote(Board board, int row, int col) {
        Piece piece = board.getPieceAt(row, col);
        if (piece == null) return;
        if (piece.getColor() == Piece.Color.RED && row == 7) piece.makeKing();
        else if (piece.getColor() == Piece.Color.BLACK && row == 0) piece.makeKing();
    }

    public static GameState.GameStatus evaluateGameStatus(GameState state) {
        Board board = state.getBoard();
        Piece.Color currentTurn = state.getCurrentTurn();

        int redCount = board.getPieceCount(Piece.Color.RED);
        int blackCount = board.getPieceCount(Piece.Color.BLACK);

        if (redCount == 0) return GameState.GameStatus.BLACK_WINS;
        if (blackCount == 0) return GameState.GameStatus.RED_WINS;

        List<Move> movesForCurrentPlayer = getAllValidMovesForPlayer(board, currentTurn);
        if (movesForCurrentPlayer.isEmpty()) {
            return (currentTurn == Piece.Color.RED) ? GameState.GameStatus.BLACK_WINS : GameState.GameStatus.RED_WINS;
        }

        // در صورت نیاز می‌توان شرایط تساوی (مثلاً تکرار حرکت) را اضافه کرد
        return GameState.GameStatus.PLAYING;
    }

    private static int[][] getMoveDirections(Piece piece) {
        if (piece.isKing()) {
            return new int[][]{{-1,-1}, {-1,1}, {1,-1}, {1,1}};
        } else {
            int forward = piece.getForwardDirection();
            return new int[][]{{forward, -1}, {forward, 1}};
        }
    }
}