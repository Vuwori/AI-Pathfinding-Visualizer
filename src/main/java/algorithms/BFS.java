package algorithms;

import maze.Cell;
import maze.Maze;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BFS extends PathfindingAlgorithm {

    @Override
    public String getName() {
        return "BFS";
    }

    @Override
    protected List<Cell> search(Maze maze, SearchListener listener) {

        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();

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

                    markVisited(maze, neighbor, listener);
                }
            }
        }

        return Collections.emptyList();
    }
}
