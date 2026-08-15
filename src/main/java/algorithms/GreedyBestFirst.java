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

//Greedy Best-First Search: always expands the cell that looks closest to
//the end, ignoring how far it has already travelled. It usually reaches
//the end after exploring very few cells, but walls can lure it down a
//long detour, so unlike A* the path it finds is not always the shortest.
public class GreedyBestFirst extends PathfindingAlgorithm {

    //order breaks ties in insertion order, so runs are repeatable
    private record Entry(Cell cell, int heuristic, long order) {
    }

    @Override
    public String getName() {
        return "Greedy Best-First";
    }

    @Override
    public String getCommandName() {
        return "greedy";
    }

    @Override
    protected List<Cell> search(Maze maze, SearchListener listener) {

        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();

        PriorityQueue<Entry> queue = new PriorityQueue<>(
                Comparator.comparingInt(Entry::heuristic)
                        .thenComparingLong(Entry::order)
        );
        Set<Cell> discovered = new HashSet<>();
        Map<Cell, Cell> previous = new HashMap<>();
        long order = 0;

        queue.offer(new Entry(start, manhattanDistance(start, end), order++));
        discovered.add(start);

        while (!queue.isEmpty()) {

            Cell current = queue.poll().cell();

            markVisited(maze, current, listener);

            if (current.equals(end)) {
                return reconstructPath(previous, end);
            }

            for (Cell neighbor : maze.getNeighbors(current)) {

                if (discovered.add(neighbor)) {
                    previous.put(neighbor, current);
                    queue.offer(new Entry(neighbor, manhattanDistance(neighbor, end), order++));
                }
            }
        }

        return Collections.emptyList();
    }
}
