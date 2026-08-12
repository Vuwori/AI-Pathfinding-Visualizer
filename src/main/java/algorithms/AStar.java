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

//A* search: like Dijkstra, but each cell is ranked by its distance from
//the start plus an estimate of the distance left to the end. The estimate
//pulls the search towards the end, so far fewer cells are explored while
//the path found is still the shortest one.
public class AStar extends PathfindingAlgorithm {

    private static final int MOVE_COST = 1;

    //estimate = distance so far + heuristic; ties go to the cell closer to the end
    private record Entry(Cell cell, int distance, int estimate, int heuristic) {
    }

    @Override
    public String getName() {
        return "A*";
    }

    @Override
    public List<Cell> findPathAnimated(Maze maze, long delayMilliseconds) {

        validateMaze(maze);

        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();

        PriorityQueue<Entry> queue = new PriorityQueue<>(
                Comparator.comparingInt(Entry::estimate)
                        .thenComparingInt(Entry::heuristic)
        );
        Map<Cell, Integer> distances = new HashMap<>();
        Set<Cell> settled = new HashSet<>();
        Map<Cell, Cell> previous = new HashMap<>();

        distances.put(start, 0);
        int startHeuristic = manhattanDistance(start, end);
        queue.offer(new Entry(start, 0, startHeuristic, startHeuristic));

        while (!queue.isEmpty()) {

            Cell current = queue.poll().cell();

            //Skip outdated queue entries for cells that are already settled
            if (!settled.add(current)) {
                continue;
            }

            markVisited(maze, current, delayMilliseconds);

            if (current.equals(end)) {
                return reconstructPath(previous, end);
            }

            for (Cell neighbor : maze.getNeighbors(current)) {

                if (settled.contains(neighbor)) {
                    continue;
                }

                int newDistance = distances.get(current) + MOVE_COST;

                if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);

                    int heuristic = manhattanDistance(neighbor, end);
                    queue.offer(new Entry(neighbor, newDistance, newDistance + heuristic, heuristic));
                }
            }
        }

        return Collections.emptyList();
    }

    //Never overestimates on a 4-directional grid, so A* stays optimal
    static int manhattanDistance(Cell from, Cell to) {
        return Math.abs(from.getRow() - to.getRow())
                + Math.abs(from.getColumn() - to.getColumn());
    }
}
