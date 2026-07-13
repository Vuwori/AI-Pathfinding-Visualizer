package maze;

import java.util.ArrayList;
import java.util.List;

public class Maze {

    private final int rows;
    private final int columns;
    private final Cell[][] grid;

    private Cell startCell;
    private Cell endCell;

    public Maze(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException(
                    "Maze dimensions must be greater than zero"
            );
        }

        this.rows = rows;
        this.columns = columns;
        this.grid = new Cell[rows][columns];

        initializeGrid();
    }

    private void initializeGrid() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                grid[row][column] =
                        new Cell(row, column, CellType.EMPTY);
            }
        }
    }

    public Cell getCell(int row, int column) {
        validatePosition(row, column);
        return grid[row][column];
    }

    public void setWall(int row, int column) {
        Cell cell = getCell(row, column);

        if (cell == startCell || cell == endCell) {
            throw new IllegalStateException(
                    "Start or end cell cannot be changed into a wall"
            );
        }

        cell.setType(CellType.WALL);
    }

    public void removeWall(int row, int column) {
        Cell cell = getCell(row, column);

        if (cell.getType() == CellType.WALL) {
            cell.setType(CellType.EMPTY);
        }
    }

    public void setStart(int row, int column) {
        Cell newStart = getCell(row, column);

        if (newStart.getType() == CellType.WALL) {
            throw new IllegalArgumentException(
                    "Start cell cannot be placed on a wall"
            );
        }

        if (startCell != null) {
            startCell.setType(CellType.EMPTY);
        }

        startCell = newStart;
        startCell.setType(CellType.START);
    }

    public void setEnd(int row, int column) {
        Cell newEnd = getCell(row, column);

        if (newEnd.getType() == CellType.WALL) {
            throw new IllegalArgumentException(
                    "End cell cannot be placed on a wall"
            );
        }

        if (endCell != null) {
            endCell.setType(CellType.EMPTY);
        }

        endCell = newEnd;
        endCell.setType(CellType.END);
    }

    public List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();

        int row = cell.getRow();
        int column = cell.getColumn();

        addWalkableNeighbor(neighbors, row - 1, column);
        addWalkableNeighbor(neighbors, row + 1, column);
        addWalkableNeighbor(neighbors, row, column - 1);
        addWalkableNeighbor(neighbors, row, column + 1);

        return neighbors;
    }

    private void addWalkableNeighbor(
            List<Cell> neighbors,
            int row,
            int column
    ) {
        if (isInsideMaze(row, column)) {
            Cell neighbor = grid[row][column];

            if (neighbor.isWalkable()) {
                neighbors.add(neighbor);
            }
        }
    }

    public void clearSearchResults() {
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                if (cell.getType() == CellType.VISITED
                        || cell.getType() == CellType.PATH) {
                    cell.setType(CellType.EMPTY);
                }
            }
        }
    }

    public boolean isInsideMaze(int row, int column) {
        return row >= 0
                && row < rows
                && column >= 0
                && column < columns;
    }

    private void validatePosition(int row, int column) {
        if (!isInsideMaze(row, column)) {
            throw new IndexOutOfBoundsException(
                    "Position outside maze: (" +
                            row + ", " + column + ")"
            );
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Cell getStartCell() {
        return startCell;
    }

    public Cell getEndCell() {
        return endCell;
    }
}