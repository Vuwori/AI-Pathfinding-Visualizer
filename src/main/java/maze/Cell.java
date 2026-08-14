package maze;

import java.util.Objects;

public class Cell {

    //Cost of stepping onto a normal cell; heavier terrain costs more
    public static final int DEFAULT_WEIGHT = 1;

    private final int row;
    private final int column;
    private CellType type;
    private int weight = DEFAULT_WEIGHT;

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

    public int getWeight() {
        return weight;
    }

    //The weight is kept separately from the type, so a mud cell stays
    //mud while it is temporarily shown as VISITED or PATH
    public void setWeight(int weight) {
        if (weight < DEFAULT_WEIGHT) {
            throw new IllegalArgumentException(
                    "Weight must be at least " + DEFAULT_WEIGHT
            );
        }

        this.weight = weight;
    }

    public boolean isWeighted() {
        return weight > DEFAULT_WEIGHT;
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
                ", weight=" + weight +
                '}';
    }
}