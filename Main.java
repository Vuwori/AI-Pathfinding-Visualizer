import algorithms.BFS;
import algorithms.DFS;
import algorithms.Dijkstra;
import algorithms.PathfindingAlgorithm;
import java.util.List;
import maze.Cell;
import maze.CellType;
import maze.Maze;
import renderer.MazeRenderer;

public class Main {

    public static void main(String[] args) {

        //Pick the algorithm from the first argument (defaults to BFS)
        String choice = args.length > 0 ? args[0] : "bfs";
        PathfindingAlgorithm algorithm = createAlgorithm(choice);

        if (algorithm == null) {
            System.out.println("Unknown algorithm: " + choice);
            System.out.println("Usage: java Main [bfs|dfs|dijkstra]");
            return;
        }

        Maze maze = new Maze(7, 17);

        maze.setStart(1, 1);
        maze.setEnd(5, 15);

        maze.setWall(1, 6);
        maze.setWall(2, 1);
        maze.setWall(2, 2);
        maze.setWall(2, 3);

        //Run the animated search
        List<Cell> path = algorithm.findPathAnimated(maze, 100);

        //Stop if no path was found
        if (path.isEmpty()) {
            System.out.println("No path found.");
            return;
        }

        //Remove all VISITED cells from the maze
        maze.clearSearchResults();

        //Mark only the final path
        for (Cell cell : path) {
            if (!cell.isStart() && !cell.isEnd()) {
                cell.setType(CellType.PATH);
            }
        }

        //Print the clean final result
        System.out.println();
        System.out.println(algorithm.getName() + " completed:");
        MazeRenderer.print(maze);

        System.out.println();
        System.out.println("Path cells: " + path.size());
        System.out.println("Moves: " + (path.size() - 1));
    }

    private static PathfindingAlgorithm createAlgorithm(String name) {
        return switch (name.toLowerCase()) {
            case "bfs" -> new BFS();
            case "dfs" -> new DFS();
            case "dijkstra" -> new Dijkstra();
            default -> null;
        };
    }
}
