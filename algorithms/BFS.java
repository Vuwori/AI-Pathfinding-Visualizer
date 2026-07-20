package algorithms;

import maze.Cell;
import maze.CellType;
import maze.Maze;
import renderer.MazeRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BFS {

    public List<Cell> findPathAnimated(Maze maze, long delayMilliseconds) {

        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();

        if (start == null || end == null) {
            throw new IllegalStateException(
                    "Maze must have both a start cell and an end cell."
            );
        }

        Queue<Cell> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();
        Map<Cell, Cell> previous = new HashMap<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {

            Cell current = queue.poll();

            if (current.equals(end)) {
                return reconstructPath(previous, end);
            }

            for (Cell neighbor : maze.getNeighbors(current)) {

                if (!visited.contains(neighbor)) {

                    visited.add(neighbor);
                    previous.put(neighbor, current);
                    queue.offer(neighbor);

                    if (!neighbor.isStart() && !neighbor.isEnd()) {
                        neighbor.setType(CellType.VISITED);
                    }

                    clearConsole();
                    MazeRenderer.print(maze);
                    sleep(delayMilliseconds);
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Cell> reconstructPath(
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
                    "BFS animation was interrupted.",
                    exception
            );
        }
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}