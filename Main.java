import algorithms.BFS;
import java.util.List;
import maze.Cell;
import maze.Maze;
import renderer.MazeRenderer;

public class Main {

    public static void main(String[] args) {

        Maze maze = new Maze(7,17);

        maze.setStart(1,1);
        maze.setEnd(5,15);

        maze.setWall(1,6);
        maze.setWall(2,1);
        maze.setWall(2,2);
        maze.setWall(2,3);

        MazeRenderer.print(maze);

        BFS bfs = new BFS();

        List<Cell> path = bfs.findPath(maze);


        System.out.println("Path cells: " + path.size());
        System.out.println("Moves: " + Math.max(0, path.size() - 1));

        for (Cell cell : path) {
            System.out.println(cell);
        }
    }
}