package algorithms;

import maze.Cell;
import maze.Maze;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//Depth-First Search: explores one branch as deep as possible before
//backtracking. It always finds a path if one exists, but not
//necessarily the shortest one.
public class DFS extends PathfindingAlgorithm {

    @Override
    public String getName() {
        return "DFS";
    }

    @Override
    protected List<Cell> search(Maze maze, SearchListener listener) {

        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();

        Deque<Cell> stack = new ArrayDeque<>();
        Set<Cell> visited = new HashSet<>();
        Map<Cell, Cell> previous = new HashMap<>();

        stack.push(start);

        while (!stack.isEmpty()) {

            Cell current = stack.pop();

            //A cell can be pushed several times before it is visited
            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);
            markVisited(maze, current, listener);

            if (current.equals(end)) {
                return reconstructPath(previous, end);
            }

            for (Cell neighbor : maze.getNeighbors(current)) {

                if (!visited.contains(neighbor)) {

                    //The most recent push wins, matching the order cells are popped
                    previous.put(neighbor, current);
                    stack.push(neighbor);
                }
            }
        }

        return Collections.emptyList();
    }
}
