package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 8x8 checkers board managing piece positions and game state.
 * Player = BLACK (bottom rows 5-7), Computer/Player2 = RED (top rows 0-2).
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;
    private Piece[][] grid;

    public Board() {
        this.grid = new Piece[8][8];
    }


    public Piece getPieceAt(int row, int col) {
        if (!isValidPosition(row, col)) return null;
        return grid[row][col];
    }

    public void setPieceAt(int row, int col, Piece piece) {
        if (!isValidPosition(row, col)) return;
        grid[row][col] = piece;
        if (piece != null) {
            piece.setRow(row);
            piece.setCol(col);
        }
    }

    public void removePiece(int row, int col) {
        if (isValidPosition(row, col)) grid[row][col] = null;
    }

    public void clear() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) grid[r][c] = null;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public boolean isDarkSquare(int row, int col) {
        return isValidPosition(row, col) && (row + col) % 2 == 1;
    }

    public boolean isEmpty(int row, int col) {
        return getPieceAt(row, col) == null;
    }

    public List<Piece> getPiecesByColor(Piece.Color color) {
        List<Piece> result = new ArrayList<>();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p != null && p.getColor() == color) result.add(p);
            }
        return result;
    }

    public int getPieceCount(Piece.Color color) {
        return getPiecesByColor(color).size();
    }


    public void initializeStandardGame() {
        clear();

        // RED pieces at top (rows 0-2) - Computer/Player 2
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 8; col++)
                if (isDarkSquare(row, col))
                    grid[row][col] = new Piece(Piece.Color.RED, row, col);

        // BLACK pieces at bottom (rows 5-7) - Player 1
        for (int row = 5; row < 8; row++)
            for (int col = 0; col < 8; col++)
                if (isDarkSquare(row, col))
                    grid[row][col] = new Piece(Piece.Color.BLACK, row, col);
    }

    public Board copy() {
        Board newBoard = new Board();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece original = grid[r][c];
                if (original != null) newBoard.setPieceAt(r, c, original.copy());
            }
        return newBoard;
    }
}