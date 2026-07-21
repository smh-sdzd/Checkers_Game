package model;

import java.io.Serializable;

/**
 * Represents a game piece with color, type, and position.
 * BLACK moves upward (decreasing row), RED moves downward (increasing row).
 */

public class Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Color { BLACK, RED }
    public enum Type { NORMAL, KING }

    private Color color;
    private Type type;
    private int row, col;

    public Piece(Color color, int row, int col) {
        this.color = color;
        this.type = Type.NORMAL;
        this.row = row;
        this.col = col;
    }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }


    public void makeKing() { this.type = Type.KING; }

    public boolean isRed() { return this.color == Color.RED; }
    public boolean isBlack() { return this.color == Color.BLACK; }
    public boolean isKing() { return this.type == Type.KING; }



    /**
     * Returns forward direction based on color.
     * BLACK moves upward (-1), RED moves downward (+1).
     */
    public int getForwardDirection() {
        return (this.color == Color.BLACK) ? -1 : 1;
    }

    public Piece copy() {
        Piece copy = new Piece(this.color, this.row, this.col);
        copy.type = this.type;
        return copy;
    }
}