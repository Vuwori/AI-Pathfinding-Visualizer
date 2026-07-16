package algorithms;

import maze.Cell;
import maze.Maze;

import java.util.*;

public class BFS {

    public List<Cell> findPath(Maze maze) {

        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();

        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> previous = new HashMap<>();
        Set<Cell> visited = new HashSet<>();

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
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Cell> reconstructPath(
            Map<Cell, Cell> previous,
            Cell end) {

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