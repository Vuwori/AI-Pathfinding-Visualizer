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

    MazeRenderer.print(maze);
        }
    }