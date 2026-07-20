package model;

import java.io.Serializable;

/**
 * Represents a move from one position to another, with optional capture.
 */
public class Move implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MoveType { NORMAL, CAPTURE }

    private final int fromRow, fromCol, toRow, toCol;
    private final MoveType type;

    public Move(int fromRow, int fromCol, int toRow, int toCol, MoveType type) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.type = type;
    }

    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }
    public MoveType getType() { return type; }

    /**
     * Returns the row of the captured piece (middle of move).
     */
    public int getCapturedRow() {
        if (type != MoveType.CAPTURE) throw new IllegalStateException("Not a capture move");
        return (fromRow + toRow) / 2;
    }


    /**
     * Returns the column of the captured piece (middle of move).
     */
    public int getCapturedCol() {
        if (type != MoveType.CAPTURE) throw new IllegalStateException("Not a capture move");
        return (fromCol + toCol) / 2;
    }

    public boolean isCapture() { return type == MoveType.CAPTURE; }
}