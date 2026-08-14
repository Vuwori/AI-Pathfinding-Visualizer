package algorithms;

import maze.Cell;
import maze.Maze;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

//Dijkstra's algorithm: always expands the cell with the lowest known
//distance from the start, so the first time the end is reached the
//path is guaranteed to be the cheapest one. Unlike BFS it takes cell
//weights into account, so it will walk around mud when that is cheaper.
public class Dijkstra extends PathfindingAlgorithm {

    private record Entry(Cell cell, int distance) {
    }

    @Override
    public String getName() {
        return "Dijkstra";
    }

    @Override
    protected List<Cell> search(Maze maze, SearchListener listener) {

        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();

        PriorityQueue<Entry> queue =
                new PriorityQueue<>(Comparator.comparingInt(Entry::distance));
        Map<Cell, Integer> distances = new HashMap<>();
        Set<Cell> settled = new HashSet<>();
        Map<Cell, Cell> previous = new HashMap<>();

        distances.put(start, 0);
        queue.offer(new Entry(start, 0));

        while (!queue.isEmpty()) {

            Cell current = queue.poll().cell();

            //Skip outdated queue entries for cells that are already settled
            if (!settled.add(current)) {
                continue;
            }

            markVisited(maze, current, listener);

            if (current.equals(end)) {
                return reconstructPath(previous, end);
            }

            for (Cell neighbor : maze.getNeighbors(current)) {

                if (settled.contains(neighbor)) {
                    continue;
                }

                int newDistance = distances.get(current) + neighbor.getWeight();

                if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    queue.offer(new Entry(neighbor, newDistance));
                }
            }
        }

        return Collections.emptyList();
    }
}
