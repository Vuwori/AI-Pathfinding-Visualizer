package maze;

import java.util.Objects;

public class Cell {

    private final int row;
    private final int column;
    private CellType type;

    public Cell(int row, int column, CellType type) {
        this.row = row;
        this.column = column;
        this.type = Objects.requireNonNull(type, "Cell type cannot be null");
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = Objects.requireNonNull(type, "Cell type cannot be null");
    }

    public boolean isWalkable() {
        return type != CellType.WALL;
    }

    public boolean isStart() {
        return type == CellType.START;
    }

    public boolean isEnd() {
        return type == CellType.END;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Cell other)) {
            return false;
        }

        return row == other.row && column == other.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "row=" + row +
                ", column=" + column +
                ", type=" + type +
                '}';
    }
}