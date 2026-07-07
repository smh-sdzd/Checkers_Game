package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Board implements Serializable {

    private static final long serialVersionUID = 1L;


    private Piece[][] grid;


    public Board() {
        this.grid = new Piece[8][8];
    }

    // copy of board
    private Board(Piece[][] grid) {
        this.grid = grid;
    }




    public Piece getPieceAt(int row, int col) {
        if (!isValidPosition(row, col)) {
            return null;
        }
        return grid[row][col];
    }

    public void setPieceAt(int row, int col, Piece piece) {
        if (!isValidPosition(row, col)) {
            return;
        }
        grid[row][col] = piece;
        if (piece != null) {
            piece.setRow(row);
            piece.setCol(col);
        }
    }


    public void removePiece(int row, int col) {
        if (isValidPosition(row, col)) {
            grid[row][col] = null;
        }
    }


    public void clear() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                grid[r][c] = null;
            }
        }
    }


    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public boolean isDarkSquare(int row, int col) {
        if (!isValidPosition(row, col)) {
            return false;
        }
        return (row + col) % 2 == 1;
    }


    public boolean isEmpty(int row, int col) {
        return getPieceAt(row, col) == null;
    }

    public List<Piece> getPiecesByColor(Piece.Color color) {
        List<Piece> result = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p != null && p.getColor() == color) {
                    result.add(p);
                }
            }
        }
        return result;
    }

    public int getPieceCount(Piece.Color color) {
        return getPiecesByColor(color).size();
    }



    public void initializeStandardGame() {
        clear();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 8; col++) {
                if (isDarkSquare(row, col)) {
                    grid[row][col] = new Piece(Piece.Color.BLACK, row, col);
                }
            }
        }

        for (int row = 5; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (isDarkSquare(row, col)) {
                    grid[row][col] = new Piece(Piece.Color.RED, row, col);
                }
            }
        }

    }


    public Board copy() {
        Board newBoard = new Board();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece original = grid[r][c];
                if (original != null) {
                    newBoard.setPieceAt(r, c, original.copy());
                }
            }
        }
        return newBoard;
    }

}
