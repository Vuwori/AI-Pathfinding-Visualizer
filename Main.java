import algorithms.BFS;
import java.util.List;
import maze.Cell;
import maze.CellType;
import maze.Maze;
import renderer.MazeRenderer;

public class Main {

    public static void main(String[] args) {

        Maze maze = new Maze(7, 17);

        maze.setStart(1, 1);
        maze.setEnd(5, 15);

        maze.setWall(1, 6);
        maze.setWall(2, 1);
        maze.setWall(2, 2);
        maze.setWall(2, 3);

        BFS bfs = new BFS();

        //Run the animated BFS search
        List<Cell> path = bfs.findPathAnimated(maze, 100);

        //Stop if no path was found
        if (path.isEmpty()) {
            System.out.println("No path found.");
            return;
        }

        //Remove all VISITED cells from the maze
        maze.clearSearchResults();

        //Mark only the final shortest path
        for (Cell cell : path) {
            if (!cell.isStart() && !cell.isEnd()) {
                cell.setType(CellType.PATH);
            }
        }

        //Print the clean final result
        System.out.println();
        System.out.println("BFS completed:");
        MazeRenderer.print(maze);

        System.out.println();
        System.out.println("Path cells: " + path.size());
        System.out.println("Moves: " + (path.size() - 1));
    }
}