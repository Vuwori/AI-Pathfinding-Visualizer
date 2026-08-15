package algorithms;

import maze.Cell;
import maze.Maze;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

//Bidirectional BFS: runs one breadth-first search from the start and
//another from the end, one layer at a time, and stops when they meet.
//Each search only has to cover about half the distance, so on open
//grids it explores far fewer cells than a single BFS. Like BFS it finds
//the path with the fewest moves and ignores cell weights.
public class BidirectionalBFS extends PathfindingAlgorithm {

    //One of the two searches: its queue and how far each reached cell is from its root
    private static final class Side {

        final Queue<Cell> queue = new ArrayDeque<>();
        final Map<Cell, Integer> distances = new HashMap<>();
        final Map<Cell, Cell> previous = new HashMap<>();

        Side(Cell root) {
            queue.offer(root);
            distances.put(root, 0);
        }
    }

    @Override
    public String getName() {
        return "Bidirectional BFS";
    }

    @Override
    public String getCommandName() {
        return "bidirectional";
    }

    @Override
    protected List<Cell> search(Maze maze, SearchListener listener) {

        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();

        Side fromStart = new Side(start);
        Side fromEnd = new Side(end);

        markVisited(maze, start, listener);
        markVisited(maze, end, listener);

        while (!fromStart.queue.isEmpty() && !fromEnd.queue.isEmpty()) {

            //Grow the smaller frontier, which keeps both searches balanced
            boolean startSide = fromStart.queue.size() <= fromEnd.queue.size();
            Side side = startSide ? fromStart : fromEnd;
            Side other = startSide ? fromEnd : fromStart;

            Cell meeting = expandLayer(maze, side, other, listener);

            if (meeting != null) {
                return joinPaths(fromStart, fromEnd, meeting);
            }
        }

        return Collections.emptyList();
    }

    //Expands one whole layer and returns the best cell where the two searches
    //touch, or null. The whole layer is finished first, because the first
    //touching cell found is not always on the shortest path.
    private Cell expandLayer(Maze maze, Side side, Side other, SearchListener listener) {
        Cell bestMeeting = null;
        int bestLength = Integer.MAX_VALUE;

        int layerSize = side.queue.size();

        for (int i = 0; i < layerSize; i++) {

            Cell current = side.queue.poll();
            int distance = side.distances.get(current) + 1;

            for (Cell neighbor : maze.getNeighbors(current)) {

                if (side.distances.containsKey(neighbor)) {
                    continue;
                }

                side.distances.put(neighbor, distance);
                side.previous.put(neighbor, current);
                side.queue.offer(neighbor);

                Integer otherDistance = other.distances.get(neighbor);

                if (otherDistance == null) {
                    markVisited(maze, neighbor, listener);
                } else if (distance + otherDistance < bestLength) {
                    bestLength = distance + otherDistance;
                    bestMeeting = neighbor;
                }
            }
        }

        return bestMeeting;
    }

    //start ... meeting comes from the start side, meeting ... end from the end side
    private List<Cell> joinPaths(Side fromStart, Side fromEnd, Cell meeting) {
        List<Cell> path = reconstructPath(fromStart.previous, meeting);

        Cell next = fromEnd.previous.get(meeting);

        while (next != null) {
            path.add(next);
            next = fromEnd.previous.get(next);
        }

        return path;
    }
}
