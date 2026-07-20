package model;
import java.io.Serializable;

public class Move implements Serializable {

    private static final long serialVersionUID = 1L;


    public enum MoveType {
        NORMAL,
        CAPTURE
    }


    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final MoveType type;


    public Move(int fromRow, int fromCol, int toRow, int toCol, MoveType type) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.type = type;
    }


    public int getFromRow() {
        return fromRow;
    }
    public int getFromCol() {
        return fromCol;
    }


    public int getToRow() {
        return toRow;
    }
    public int getToCol() {
        return toCol;
    }


    public MoveType getType() {
        return type;
    }


    public int getCapturedRow() {
        if (type != MoveType.CAPTURE) {
            throw new IllegalStateException("Cannot get captured row for a non-capture move");
        }
        return (fromRow + toRow) / 2;
    }
    public int getCapturedCol() {
        if (type != MoveType.CAPTURE) {
            throw new IllegalStateException("Cannot get captured col for a non-capture move");
        }
        return (fromCol + toCol) / 2;
    }


    public boolean isCapture() {
        return this.type == MoveType.CAPTURE;
    }


    public int getRowDelta() {
        return Math.abs(toRow - fromRow);
    }
    public int getColDelta() {
        return Math.abs(toCol - fromCol);
    }
}