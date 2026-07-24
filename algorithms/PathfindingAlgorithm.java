package algorithms;

import maze.Cell;
import maze.CellType;
import maze.Maze;
import renderer.MazeRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class PathfindingAlgorithm {

    public abstract String getName();

    public abstract List<Cell> findPathAnimated(Maze maze, long delayMilliseconds);

    protected void validateMaze(Maze maze) {
        if (maze.getStartCell() == null || maze.getEndCell() == null) {
            throw new IllegalStateException(
                    "Maze must have both a start cell and an end cell."
            );
        }
    }

    //Mark a cell as VISITED and draw the next animation frame
    protected void markVisited(Maze maze, Cell cell, long delayMilliseconds) {
        if (!cell.isStart() && !cell.isEnd()) {
            cell.setType(CellType.VISITED);
        }

        clearConsole();
        MazeRenderer.print(maze);
        sleep(delayMilliseconds);
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

    private void sleep(long delayMilliseconds) {
        try {
            Thread.sleep(delayMilliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                    getName() + " animation was interrupted.",
                    exception
            );
        }
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
