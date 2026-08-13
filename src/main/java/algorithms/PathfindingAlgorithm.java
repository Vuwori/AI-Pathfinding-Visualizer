package algorithms;

import maze.Cell;
import maze.CellType;
import maze.Maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class PathfindingAlgorithm {

    public abstract String getName();

    //The actual search; every explored cell must go through markVisited
    protected abstract List<Cell> search(Maze maze, SearchListener listener);

    public List<Cell> findPath(Maze maze) {
        return findPath(maze, SearchListener.NONE);
    }

    public List<Cell> findPath(Maze maze, SearchListener listener) {
        validateMaze(maze);
        return search(maze, Objects.requireNonNull(listener, "Listener cannot be null"));
    }

    @Override
    public String toString() {
        return getName();
    }

    protected void validateMaze(Maze maze) {
        if (maze.getStartCell() == null || maze.getEndCell() == null) {
            throw new IllegalStateException(
                    "Maze must have both a start cell and an end cell."
            );
        }
    }

    //Mark a cell as VISITED and tell the listener about it
    protected void markVisited(Maze maze, Cell cell, SearchListener listener) {
        if (!cell.isStart() && !cell.isEnd()) {
            cell.setType(CellType.VISITED);
        }

        listener.onVisit(maze, cell);
    }

    protected List<Cell> reconstructPath(
            Map<Cell, Cell> previous,
            Cell end
    ) {
        List<Cell> path = new ArrayList<>();

        Cell current = end;

        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        Collections.reverse(path);
        return path;
    }
}
